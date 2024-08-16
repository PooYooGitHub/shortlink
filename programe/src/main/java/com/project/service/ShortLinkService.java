package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.ShortLinkDO;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO requestParam);
}
