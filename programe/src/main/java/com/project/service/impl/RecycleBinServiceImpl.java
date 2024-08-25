package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.dao.entity.ShortLinkDO;
import com.project.dao.mapper.ShortLinkMapper;
import com.project.dto.req.ShortLinkRecoverReqDTO;
import com.project.dto.req.ShortLinkToRecycleBinReqDTO;
import com.project.dto.req.pageRecycleBinShortLinkReqDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
import com.project.service.RecycleBinService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.project.common.constant.RedisKeyConstant.GO_TO_IS_NULL_SHORT_LINK_KEY;
import static com.project.common.constant.RedisKeyConstant.GO_TO_SHORT_LINK_KEY;

/**
 * 回收站服务实现层
 */
@Service
@AllArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public void moveShortLinkToRecycleBin(ShortLinkToRecycleBinReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setEnableStatus(1);
        baseMapper.update(shortLinkDO, eq);
        stringRedisTemplate.delete(String.format(GO_TO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
        return ;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(pageRecycleBinShortLinkReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, requestParam.getGids())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> page = baseMapper.selectPage(requestParam, eq);
        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));

    }

    @Override
    public void recoverShortLink(ShortLinkRecoverReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setEnableStatus(0);
        int update = baseMapper.update(shortLinkDO, eq);
        stringRedisTemplate.delete(String.format(GO_TO_IS_NULL_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
        return ;
    }
}
