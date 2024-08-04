package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.UserDO;
import com.project.dto.req.UserLoginReqDTO;
import com.project.dto.req.UserRegisterReqDTO;
import com.project.dto.req.UserUpdateReqDTO;
import com.project.dto.resp.UserLoginRespDTO;
import com.project.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username
     * @return 用户存在返回True，不存在返回False
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名修改用户
     * @param requestParam 修改用户实体
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 根据用户名和密码登录
     * @param requestParam
     * @return
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);
}
