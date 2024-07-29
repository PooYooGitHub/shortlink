package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.UserDO;
import com.project.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);
}
