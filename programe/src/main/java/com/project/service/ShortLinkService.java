package com.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.ShortLinkDO;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.req.ShortLinkPageReqDTO;
import com.project.dto.req.ShortLinkUpdateReqDTO;
import com.project.dto.resp.GroupShortLinkCountRespDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

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

    /**
     * 修改短链接相关信息
     * @param requestParam 请求参数实体
     */
    void updateGroup(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转功能
     * @param shortUri 短链接uri
     * @param request 请求
     * @param response 响应
     */
    void shortLinkRedirect(String shortUri, ServletRequest request, ServletResponse response) ;
}
