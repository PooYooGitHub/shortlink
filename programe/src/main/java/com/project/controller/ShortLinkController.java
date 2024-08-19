package com.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.ShortLinkCreateReqDTO;
import com.project.dto.req.ShortLinkPageReqDTO;
import com.project.dto.resp.GroupShortLinkCountRespDTO;
import com.project.dto.resp.ShortLinkCreateRespDTO;
import com.project.dto.resp.ShortLinkPageRespDTO;
import com.project.service.ShortLinkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam){
        return Results.success(shortLinkService.create(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> queryPage(@ModelAttribute ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.queryPage(requestParam));
    }
    /**
     * 查询分组下短链接的数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<GroupShortLinkCountRespDTO>> countShortLinkInGroup(@RequestParam("gid") List<String> gids){
        return Results.success(shortLinkService.countShortLinkInGroup(gids));

    }
}
