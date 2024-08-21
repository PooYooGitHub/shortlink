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
import com.project.dao.mapper.ShortLinkMapper;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.req.ShortLinkPageReqDTO;
import com.project.dto.req.ShortLinkUpdateReqDTO;
import com.project.dto.resp.GroupShortLinkCountRespDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
import com.project.service.ShortLinkService;
import com.project.util.HashUtil;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 短链接接口实现层
 */

@Service
@AllArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;


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
        //由于可能要修改分组，而分组gid是t_link的分片键，所以只能先删除再插入
        baseMapper.deleteById(shortLinkDO.getId());
        shortLinkDO.setGid(requestParam.getGid());
        shortLinkDO.setDescribe(requestParam.getDescribe());
        shortLinkDO.setValidDateType(requestParam.getValidDateType());
        shortLinkDO.setValidDate(requestParam.getValidDate());
        shortLinkDO.setOriginUrl(requestParam.getOriginUrl());
        baseMapper.insert(shortLinkDO);
    }

}
