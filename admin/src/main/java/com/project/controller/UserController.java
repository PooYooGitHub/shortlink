package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.dto.req.UserRegisterReqDTO;
import com.project.dto.resp.UserRespDTO;
import com.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return 用户信息
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));

    }

    /**
     * 根据用户名查询用户是否存在
     * @param username
     * @return True:存在，False:不存在
     */

    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 用户请求实体对象（初始是请求体中的json对象，后面处理了）
     * @return 注册成功（不成功会在异常中处理）
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO userRegisterReqDTO){
        userService.register(userRegisterReqDTO);
        return Results.success();
    }
}
