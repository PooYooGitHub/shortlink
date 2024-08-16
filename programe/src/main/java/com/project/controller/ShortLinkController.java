package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.service.ShortLinkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接控制层
 */
@AllArgsConstructor
@RestController
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam){
        return Results.success(shortLinkService.create(requestParam));
    }
}
