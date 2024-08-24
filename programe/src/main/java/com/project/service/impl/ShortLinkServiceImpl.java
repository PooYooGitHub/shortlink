package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.convention.exception.ClientException;
import com.project.dao.entity.ShortLinkDO;
import com.project.dao.entity.ShortLinkGoToDO;
import com.project.dao.mapper.ShortLinkGoToMapper;
import com.project.dao.mapper.ShortLinkMapper;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.req.ShortLinkPageReqDTO;
import com.project.dto.req.ShortLinkUpdateReqDTO;
import com.project.dto.resp.GroupShortLinkCountRespDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
import com.project.service.ShortLinkService;
import com.project.util.HashUtil;
import com.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.project.common.constant.RedisKeyConstant.GO_TO_IS_NULL_SHORT_LINK_KEY;
import static com.project.common.constant.RedisKeyConstant.GO_TO_SHORT_LINK_KEY;

/**
 * 短链接接口实现层
 */

@Service
@AllArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;
    private final ShortLinkGoToMapper ShortLinkGoToMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    //TODO:这里没有校验分组名是否存在该用户里面
    public ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO requestParam) {

        // 生成短链接使用的是hash算法，可能会存在hash冲突，需要处理
        //将 新生成的url存入布隆过滤器,好像可以解决

        String shortUri;
        String shortUrl;
        do {
            shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl() + System.currentTimeMillis());
            shortUrl = requestParam.getDomain() + "/" + shortUri;
        } while (shortLinkCachePenetrationBloomFilter.contains(shortUrl));

        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortUri);
        shortLinkDO.setFullShortUrl(shortUrl);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setFavicon(getFavicon(requestParam.getOriginUrl()));

        baseMapper.insert(shortLinkDO);
        //将新生成的短链接存入布隆过滤器,防止下次生成的短链接重复
        shortLinkCachePenetrationBloomFilter.add(shortUrl);
        //将短链接和gid的对应关系存入goto表
        ShortLinkGoToMapper.insert(ShortLinkGoToDO.builder().shortUrl(shortUrl).gid(requestParam.getGid()).build());
        //将短链接和gid存入redis，方便下次跳转，不用查询数据库,这个叫缓存预热，防止缓存击穿和缓存雪崩
        stringRedisTemplate.opsForValue().set(String.format(GO_TO_SHORT_LINK_KEY, shortUrl), requestParam.getOriginUrl(), LinkUtil.getShortLinkCacheTime(requestParam.getValidDate()), TimeUnit.MILLISECONDS);

        return ShortLinkCreateRespDTO
                .builder().
                gid(requestParam.getGid()).
                originUrl(requestParam.getOriginUrl()).
                fullShortUrl(shortLinkDO.getFullShortUrl())
                .build();
    }


    @Override
    public IPage<ShortLinkPageRespDTO> queryPage(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        Page<ShortLinkDO> shortLinkDOPage = baseMapper.selectPage(requestParam, eq);
        return shortLinkDOPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));

    }

    @Override
    public List<GroupShortLinkCountRespDTO> countShortLinkInGroup(List<String> gids) {
        QueryWrapper<ShortLinkDO> eq = Wrappers
                .query(new ShortLinkDO())
                .select("gid,count(*) as shortLinkCount")
                .in("gid", gids)
                .eq("enable_status", 0)
                .groupBy("gid");
        //groupByGID不能少,不然就是查的所有
        List<Map<String, Object>> list = baseMapper.selectMaps(eq);
        return BeanUtil.copyToList(list, GroupShortLinkCountRespDTO.class);


    }

    @Override
    //TODO：数据库表的插入时间和更新时间与真实插入时间相差几个小时，需要修改
    //TODO：validdatetype如果从1改为0，validdate的值不会改变，也就是改为永久有效时，还是存在过期时间
    public void updateGroup(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(eq);
        if (shortLinkDO == null) {
            throw new ClientException("短链接不存在");
        }

        shortLinkDO.setDescribe(requestParam.getDescribe());
        shortLinkDO.setValidDateType(requestParam.getValidDateType());
        shortLinkDO.setValidDate(requestParam.getValidDate());
        shortLinkDO.setOriginUrl(requestParam.getOriginUrl());
        shortLinkDO.setFavicon(getFavicon(requestParam.getOriginUrl()));
        //由于可能要修改分组，而分组gid是t_link的分片键，所以只能先删除再插入
        if (!requestParam.getGid().equals(requestParam.getOriginGid())) {
            //说明分组发生了变化
            LambdaQueryWrapper<ShortLinkDO> eq2 = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
            baseMapper.delete(eq2);
            shortLinkDO.setGid(requestParam.getGid());
            baseMapper.insert(shortLinkDO);
            //更新短链接的goto表
            ShortLinkGoToDO shortLinkGoToDO = ShortLinkGoToDO.builder()
                    .gid(requestParam.getGid()).build();
            LambdaQueryWrapper<ShortLinkGoToDO> eq1 = Wrappers.lambdaQuery(ShortLinkGoToDO.class)
                    .eq(ShortLinkGoToDO::getShortUrl, shortLinkDO.getFullShortUrl());
            ShortLinkGoToMapper.update(shortLinkGoToDO, eq1);
        } else {
            //由于gid是分片键，更新时gid不能变，更新的实体不能携带gid
            Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getOriginGid());
            baseMapper.update(shortLinkDO, eq);
        }
        //更新redis中的短链接和原始链接的对应关系
        stringRedisTemplate.opsForValue().set(String.format(GO_TO_SHORT_LINK_KEY, shortLinkDO.getFullShortUrl()), requestParam.getOriginUrl(), LinkUtil.getShortLinkCacheTime(requestParam.getValidDate()), TimeUnit.MILLISECONDS);

    }

    @Override

    /**
     * 解决
     *  缓存击穿：数据在缓存中不存在，但在数据库中存在
     *  缓存穿透：数据在缓存和数据库中都不存在
     *  的解决思路
     * 首先查询
     *  缓存：存放短链接与原始链接的对应关系，在创建短链接时创建，修改短链接时修改
     * 如果缓存中存在，直接定向；不存在，准备查询数据库，将数据库的短链接加入缓存
     * 这时要判断数据库中存不存在短链接
     * 然后查询
     *  布隆过滤器：存放短链接，在短链接创建的时候将短链接加入其中
     * 如果布隆过滤器中不存在，说明一定不存在该短链接，返回404，并将不存在的短链接加入
     *  短链接不存在缓存： key：不存在的短链接，value：”-“
     * 如果布隆过滤其中存在短链接，说明数据库可能存在目标短链接
     * 然后加锁                                     <<<<------------------------------------------------
     * ------------双重判定                                                                        |
     * 先查询短链接不存在缓存，如果里面有值，说明短链接在数据库中不存在，直接返回404                  |
     * 然后查询缓存，如果缓存中存在。。。。。。                                                     |
     * --------------                                                                            |
     * 查询数据库，如果短链接存在，将短链接和原始链接加入缓存;如果不存在将短链接加入短链接不存在缓存---
     *
     *
     * TODO：用户删除短链接，但是数据库中是软删除，这时再访问缓存，这个短链接还是有效的,如何解决，？
     *  删除短链接时删除缓存
     *
     * -----------问题-------------------
     * 如果http://www.shyu.com/3GbK8q1在前面已经被加入短链接不存在缓存，同时缓存已经失效。
     * 但是这时由用户生成了一个http://www.shyu.com/3GbK8q1的短链接，那这个短链接不就失效了？
     * 解决方法： TODO：添加短链接时尝试删除`短链接不存在缓存`中的相关数据
     */
    public void shortLinkRedirect(String shortUri, ServletRequest request, ServletResponse response) {
        //由于短链接的分片键是gid，查询尽量用gid查询，不然就会查询所有的表，
        //所以这里我创建了一个新的表，用来存储短链接和gid的对应关系goto
        String protocol = ((HttpServletRequest) request).getScheme();
        String shortUrl = protocol + "://" + request.getServerName() + "/" + shortUri;
        String originalUrl = stringRedisTemplate.opsForValue().get(String.format(GO_TO_SHORT_LINK_KEY, shortUrl));
        if (StringUtil.isBlank(originalUrl)) {
            //redis中没有短链接和原始链接的对应关系，查询数据库
            //防止数据库中也没有数据，也就是缓存穿透问题，使用布隆过滤器+分布式锁
            if (!shortLinkCachePenetrationBloomFilter.contains(shortUrl)) {
                //说明数据库中一定没有该短链接
                stringRedisTemplate.opsForValue().set(String.format(GO_TO_IS_NULL_SHORT_LINK_KEY, shortUrl), "-", 30, TimeUnit.DAYS);
                ((HttpServletResponse) response).setStatus(404);
                return;
            }
            //数据库中存在短链接，但是可能误判
            if (StringUtil.isNotBlank(stringRedisTemplate.opsForValue().get(String.format(GO_TO_IS_NULL_SHORT_LINK_KEY, shortUrl)))) {
                ((HttpServletResponse) response).setStatus(404);
                return;
            }
            RLock lock = redissonClient.getLock(String.format("short-link_lock_go_to:%s", shortUrl));

            lock.lock();
            try {
                if (StringUtil.isNotBlank(stringRedisTemplate.opsForValue().get(String.format(GO_TO_IS_NULL_SHORT_LINK_KEY, shortUrl)))) {
                    ((HttpServletResponse) response).setStatus(404);
                    return;
                }
                originalUrl = stringRedisTemplate.opsForValue().get(String.format(GO_TO_SHORT_LINK_KEY, shortUrl));
                if (StringUtil.isNotBlank(originalUrl)) {
                    //如果在获取锁的过程中，其他线程已经获取到锁并且已经设置了短链接和原始链接的对应关系，那么直接跳转
                    ((HttpServletResponse) response).sendRedirect(originalUrl);
                    return;
                }

                LambdaQueryWrapper<ShortLinkGoToDO> eq = Wrappers.lambdaQuery(ShortLinkGoToDO.class)
                        .eq(ShortLinkGoToDO::getShortUrl, shortUrl);
                ShortLinkGoToDO shortLinkGoToDO = ShortLinkGoToMapper.selectOne(eq);
                if (shortLinkGoToDO == null) {
                    //如果短链接不存在，返回404
                    stringRedisTemplate.opsForValue().set(String.format(GO_TO_IS_NULL_SHORT_LINK_KEY, shortUrl), "-", 30, TimeUnit.DAYS);
                    ((HttpServletResponse) response).setStatus(404);
                    return;
                }
                //如果短链接存在，重定向到原始链接
                LambdaQueryWrapper<ShortLinkDO> eq1 = Wrappers.lambdaQuery(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid, shortLinkGoToDO.getGid())
                        .eq(ShortLinkDO::getFullShortUrl, shortUrl)
                        .eq(ShortLinkDO::getEnableStatus, 0)
                        .eq(ShortLinkDO::getDelFlag, 0);
                ShortLinkDO shortLinkDO = baseMapper.selectOne(eq1);
                if (shortLinkDO == null) {
                    //如果短链接不存在，返回404
                    ((HttpServletResponse) response).setStatus(404);
                    return;
                }
                if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(DateTime.now())) {
                    //短链接不是永久有效，并且已经过期
                    ((HttpServletResponse) response).setStatus(404);
                    return;
                }
                //是永久有效，并且没有过期
                stringRedisTemplate.opsForValue().set(String.format(GO_TO_SHORT_LINK_KEY, shortUrl), shortLinkDO.getOriginUrl(), LinkUtil.getShortLinkCacheTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS);
                originalUrl = shortLinkDO.getOriginUrl();

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

        try {
            ((HttpServletResponse) response).sendRedirect(originalUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return;
    }


    /**
     * 获取网站的favicon图标链接
     *
     * @param url 网站的URL
     * @return favicon图标链接,如果不存在则返回null
     */
    @SneakyThrows
    private String getFavicon(String url) {

        // 创建URL对象
        URL targetUrl = new URL(url);
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        // 禁止自动处理重定向
        connection.setInstanceFollowRedirects(false);
        // 设置请求方法为GET
        connection.setRequestMethod("GET");
        // 连接
        connection.connect();
        // 获取响应码
        int responseCode = connection.getResponseCode();
        // 如果是重定向响应码
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            // 获取重定向的URL
            String redirectUrl = connection.getHeaderField("Location");
            // 如果重定向URL不为空
            if (redirectUrl != null) {
                // 创建新的URL对象
                URL newUrl = new URL(redirectUrl);
                // 打开新的连接
                connection = (HttpURLConnection) newUrl.openConnection();
                // 设置请求方法为GET
                connection.setRequestMethod("GET");
                // 连接
                connection.connect();
                // 获取新的响应码
                responseCode = connection.getResponseCode();
            }
        }
        // 如果响应码为200(HTTP_OK)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 使用Jsoup库连接到URL并获取文档对象
            Document document = Jsoup.connect(url).get();
            // 选择第一个匹配的<link>标签，其rel属性包含"shortcut"或"icon"
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut|icon)]").first();
            // 如果存在favicon图标链接
            if (faviconLink != null) {
                // 返回图标链接的绝对路径
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }
}


