package com.project.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户持久层实体
 */

@TableName("t_user")
@Data
public class UserDO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 注销时间戳
     */
    private Long delTime;

    /**
     * 删除标识 0：未删除，1已删除
     */
    private int delFlag;

}
