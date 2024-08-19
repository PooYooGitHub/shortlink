package com.project.dto.req;

import lombok.Data;

/**
 * 分组短链接统计请求实体
 */
@Data
public class GroupShortLinkCountReqDTO {
    /**
     * 分组标识
     */
    private String gid;
}
