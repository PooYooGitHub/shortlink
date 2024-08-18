package com.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.remote.dto.ShortLinkRemoteService;
import com.project.remote.dto.req.ShortLinkCreateReqDTO;
import com.project.remote.dto.req.ShortLinkPageReqDTO;
import com.project.remote.dto.resp.ShortLinkCreateRespDTO;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.*;

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
    * 分页查询短链接
    */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> queryPage(@ModelAttribute ShortLinkPageReqDTO requestParam){
        return shortLinkRemoteService.queryPage(requestParam);
    }
}
