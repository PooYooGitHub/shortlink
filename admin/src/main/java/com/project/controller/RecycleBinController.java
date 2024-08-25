package com.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.remote.dto.RecycleBinRemoteService;
import com.project.remote.dto.req.ShortLinkRecoverReqDTO;
import com.project.remote.dto.req.ShortLinkRemoveReqDTO;
import com.project.remote.dto.req.ShortLinkToRecycleBinReqDTO;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;
import com.project.service.RecycleBinService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class RecycleBinController {
    private final RecycleBinRemoteService recycleBinRemoteService=new RecycleBinRemoteService() {
    };
    private final RecycleBinService recycleBinService;

    /**
     * 将短链接移至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> moveShortLinkToRecycleBin(@RequestBody ShortLinkToRecycleBinReqDTO requestParam) {
        recycleBinRemoteService.moveShortLinkToRecycleBin(requestParam);
        return Results.success();

    }

    /**
     * 分页查询用户回收站的短链接
     * @param current
     * @param size
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@RequestParam("current") String current, @RequestParam("size") String size) {
        return recycleBinService.pageRecycleBinShortLink(current, size);
    }

    /**
     * 从回收站恢复短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverShortLink(@RequestBody ShortLinkRecoverReqDTO requestParam) {
        recycleBinRemoteService.recoverShortLink(requestParam);
        return Results.success();
    }
    /**
     * 从回收站删除短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void> removeShortLink(@RequestBody ShortLinkRemoveReqDTO requestParam) {
        recycleBinRemoteService.removeShortLink(requestParam);
        return Results.success();
    }

}
