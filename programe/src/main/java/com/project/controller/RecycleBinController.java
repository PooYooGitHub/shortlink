package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.ShortLinkToRecycleBinReqDTO;
import com.project.service.RecycleBinService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
/**
 * 回收站控制层
 */
public class RecycleBinController {
    private final RecycleBinService recycleBinService;

    /**
     * 将短链接移至回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> moveShortLinkToRecycleBin(@RequestBody ShortLinkToRecycleBinReqDTO requestParam) {
        recycleBinService.moveShortLinkToRecycleBin(requestParam);
        return Results.success();

    }
}
