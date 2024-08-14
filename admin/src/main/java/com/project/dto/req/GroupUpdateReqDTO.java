package com.project.dto.req;

import lombok.Data;

@Data
public class GroupUpdateReqDTO {
    /**
     * 分组标识gid
     */
    private String gid;
    /**
     * 被修改后的分组名
     */
    private String name;
}
