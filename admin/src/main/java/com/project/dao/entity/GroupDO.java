package com.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @description t_group
 * @author zhengkai.blog.csdn.net
 * @date 2024-08-11
 */
@Data
@TableName("t_group")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;



}
