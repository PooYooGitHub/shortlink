package com.project.dto.resp;

import lombok.Data;

@Data
public class GroupingRespDTO {
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
     * 分组下短链接的数量
     */
    private Integer shortLinkCount;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
