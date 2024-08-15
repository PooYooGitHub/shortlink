package com.project.dto.req;

import lombok.Data;

@Data
public class GroupSortReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 序号
     */
    private Integer sortOrder;
}
