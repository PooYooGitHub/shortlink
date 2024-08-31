package com.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.remote.dto.ShortLinkRemoteService;
import com.project.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.project.remote.dto.req.ShortLinkCreateReqDTO;
import com.project.remote.dto.req.ShortLinkPageReqDTO;
import com.project.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import com.project.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.project.remote.dto.resp.ShortLinkCreateRespDTO;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;
import com.project.util.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接后管控制层(通过接口创建,查询短链接,并发要求高)
 */
@RestController
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
    * 创建短链接
    */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/api/short-link/admin/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = shortLinkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

    /**
    * 分页查询短链接
    */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> queryPage(@ModelAttribute ShortLinkPageReqDTO requestParam){
        return shortLinkRemoteService.queryPage(requestParam);
    }
}
