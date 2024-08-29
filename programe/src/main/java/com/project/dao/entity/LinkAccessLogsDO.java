package com.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问日志监控实体
 */
@Data
@TableName("t_link_access_logs")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkAccessLogsDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备
     */
    private String device;

    /**
     * 网络
     */
    private String network;

    /**
     * 访问地址
     */
    private String locate;

    /**
     * ip
     */
    private String ip;


}
