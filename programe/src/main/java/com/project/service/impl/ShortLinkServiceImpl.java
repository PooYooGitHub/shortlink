package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.dao.entity.ShortLinkDO;
import com.project.dao.mapper.ShortLinkMapper;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.req.ShortLinkPageReqDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
import com.project.service.ShortLinkService;
import com.project.util.HashUtil;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO requestParam) {

        // 生成短链接使用的是hash算法，可能会存在hash冲突，需要处理
        //将 新生成的url存入布隆过滤器,好像可以解决

        String shortUri ;
        String shortUrl ;
        do {
            shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl()+System.currentTimeMillis());
            shortUrl = requestParam.getDomain() + "/" + shortUri;
        }while (shortLinkCachePenetrationBloomFilter.contains(shortUrl));

        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortUri);
        shortLinkDO.setFullShortUrl(shortUrl);

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
        return shortLinkDOPage.convert(each-> BeanUtil.toBean(each,ShortLinkPageRespDTO.class));

    }


}
