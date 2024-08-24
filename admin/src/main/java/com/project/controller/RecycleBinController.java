package com.project.controller;


import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.remote.dto.RecycleBinRemoteService;
import com.project.remote.dto.req.ShortLinkToRecycleBinReqDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecycleBinController {
    private final RecycleBinRemoteService recycleBinRemoteService=new RecycleBinRemoteService() {
    };

    /**
     * 将短链接移至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> moveShortLinkToRecycleBin(@RequestBody ShortLinkToRecycleBinReqDTO requestParam) {
        recycleBinRemoteService.moveShortLinkToRecycleBin(requestParam);
        return Results.success();

    }
}
