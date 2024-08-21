package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 短链接接口实现层
 */

@Service
@AllArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;
    private final ShortLinkGoToMapper ShortLinkGoToMapper;


    @Override
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

        baseMapper.insert(shortLinkDO);
        shortLinkCachePenetrationBloomFilter.add(shortUrl);
        ShortLinkGoToMapper.insert(ShortLinkGoToDO.builder().shortUrl(shortUrl).gid(requestParam.getGid()).build());


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
        List<Map<String,Object>> list = baseMapper.selectMaps(eq);
        return BeanUtil.copyToList(list, GroupShortLinkCountRespDTO.class);


    }

    @Override
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
        //由于可能要修改分组，而分组gid是t_link的分片键，所以只能先删除再插入
        if (!requestParam.getGid().equals(requestParam.getOriginGid())) {
            //说明分组发生了变化
            baseMapper.deleteById(shortLinkDO.getId());
            shortLinkDO.setGid(requestParam.getGid());
            baseMapper.insert(shortLinkDO);
            //更新短链接的goto表
            ShortLinkGoToDO shortLinkGoToDO = ShortLinkGoToDO.builder()
                    .gid(requestParam.getGid()).build();
            LambdaQueryWrapper<ShortLinkGoToDO> eq1 = Wrappers.lambdaQuery(ShortLinkGoToDO.class)
                    .eq(ShortLinkGoToDO::getShortUrl, shortLinkDO.getFullShortUrl());
            ShortLinkGoToMapper.update(shortLinkGoToDO, eq1);
        }else {
            //这里由于gid是分片键，更新的实体不能携带gid，所以要先将实体gid设置为null，然后再更新
            shortLinkDO.setGid(null);
            baseMapper.updateById(shortLinkDO);
        }

    }

    @Override
    public void shortLinkRedirect(String shortUri, ServletRequest request, ServletResponse response)  {
        //由于短链接的分片键是gid，查询尽量用gid查询，不然就会查询所有的表，
        //所以这里我创建了一个新的表，用来存储短链接和gid的对应关系goto
        String protocol= ((HttpServletRequest) request).getScheme();
        String shortUrl = protocol +request.getServerName() + "/" + shortUri;
        LambdaQueryWrapper<ShortLinkGoToDO> eq = Wrappers.lambdaQuery(ShortLinkGoToDO.class)
                .eq(ShortLinkGoToDO::getShortUrl, shortUrl);
        ShortLinkGoToDO shortLinkGoToDO = ShortLinkGoToMapper.selectOne(eq);
        if (shortLinkGoToDO == null) {
            //如果短链接不存在，返回404
            ((HttpServletResponse)response).setStatus(404);
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

        try {
            ((HttpServletResponse)response).sendRedirect(shortLinkDO.getOriginUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
