package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.GroupAddReqDTO;
import com.project.dto.resp.GroupingRespDTO;
import com.project.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
* 短链接分租控制层
 */
@RestController
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增分组
     */
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> addGroup(@RequestBody GroupAddReqDTO requestParam) {
        groupService.addGroup(requestParam);
        return Results.success();
    }

    /**
     * 查询分组
     */
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupingRespDTO>> queryGroup(){
        return Results.success(groupService.queryGroup());
    }
}
