package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.GroupAddReqDTO;
import com.project.dto.req.GroupSortReqDTO;
import com.project.dto.req.GroupUpdateReqDTO;
import com.project.dto.resp.GroupingRespDTO;
import com.project.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改分组名
     */
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> updateGroup(@RequestBody GroupUpdateReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/api/short-link/admin/v1/group")
    public Result<Void> deleteGroup(@RequestParam String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 给分组排序
     */
    @PostMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sortGroup(@RequestBody List<GroupSortReqDTO> requestParam) {
        groupService.sortGroup(requestParam);
        return Results.success();

    }
}
