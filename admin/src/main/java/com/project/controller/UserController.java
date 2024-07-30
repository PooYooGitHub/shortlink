package com.project.controller;

import com.project.common.constant.result.Result;
import com.project.common.enums.UserErrorCode;
import com.project.dto.resp.UserRespDTO;
import com.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserRespDTO user = userService.getUserByUsername(username);
        if (user == null) {
            return new Result<UserRespDTO>().setCode(UserErrorCode.USER_NUll.code()).setMessage(UserErrorCode.USER_NUll.message());
        }else {
            return new Result<UserRespDTO>().setCode("0").setMessage("成功").setData(user);
        }
    }
}
