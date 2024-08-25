package com.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.ShortLinkRecoverReqDTO;
import com.project.dto.req.ShortLinkToRecycleBinReqDTO;
import com.project.dto.req.ShortLinkPageRecycleBinReqDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
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
    /**
     * 分页查询用户回收站的短链接
     */
    //TODO：这个接口是有问题的，只能从admin调才行
    @PostMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@RequestBody ShortLinkPageRecycleBinReqDTO requestParam) {

        return Results.success(recycleBinService.pageRecycleBinShortLink(requestParam));

    }
    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverShortLink(@RequestBody ShortLinkRecoverReqDTO requestParam) {
        recycleBinService.recoverShortLink(requestParam);
        return Results.success();
    }
}
