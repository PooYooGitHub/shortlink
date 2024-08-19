package com.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.ShortLinkDO;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.req.ShortLinkPageReqDTO;
import com.project.dto.resp.GroupShortLinkCountRespDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

/**
 * 短链接接口
 */

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     * @param requestParam 请求参数实体
     * @return 分页数据
     */
    IPage<ShortLinkPageRespDTO> queryPage(ShortLinkPageReqDTO requestParam);

    /**
     * 查询分组下短链接的数量
     * @param gids 需要查询的分组id
     * @return
     */
    List<GroupShortLinkCountRespDTO> countShortLinkInGroup(List<String> gids);
}
