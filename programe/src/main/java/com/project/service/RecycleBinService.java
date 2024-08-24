package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.ShortLinkDO;
import com.project.dto.req.ShortLinkToRecycleBinReqDTO;

/**
 * 回收站服务接口
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 将短链接移至回收站
     * @param requestParam 请求实体
     * @return
     */
    default void moveShortLinkToRecycleBin(ShortLinkToRecycleBinReqDTO requestParam){

    }

}
