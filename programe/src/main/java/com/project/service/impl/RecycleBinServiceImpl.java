package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.dao.entity.ShortLinkDO;
import com.project.dao.mapper.ShortLinkMapper;
import com.project.dto.req.ShortLinkToRecycleBinReqDTO;
import com.project.service.RecycleBinService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.project.common.constant.RedisKeyConstant.GO_TO_SHORT_LINK_KEY;

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
}
