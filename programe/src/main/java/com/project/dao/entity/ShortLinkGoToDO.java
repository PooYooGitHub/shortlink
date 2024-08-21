package com.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_link_goto")
@Data
public class ShortLinkGoToDO {
    /**
     * id
     */
    private Long id;
    /**
     * 短链接分组标识gid
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String shortUri;

}
