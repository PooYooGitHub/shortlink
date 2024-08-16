package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.convention.exception.ClientException;
import com.project.dao.entity.ShortLinkDO;
import com.project.dao.mapper.ShortLinkMapper;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.service.ShortLinkService;
import com.project.util.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    @Override
    public ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO requestParam) {
        String shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl());

        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortUri);
        shortLinkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortUri);

        try {
            baseMapper.insert(shortLinkDO);
        } catch (Exception e) {
            throw new ClientException("短链接已经存在,请勿重复创建");
        }

        return ShortLinkCreateRespDTO
                .builder().
                gid(requestParam.getGid()).
                originUrl(requestParam.getOriginUrl()).
                fullShortUrl(shortLinkDO.getFullShortUrl())
                .build();
    }

}
