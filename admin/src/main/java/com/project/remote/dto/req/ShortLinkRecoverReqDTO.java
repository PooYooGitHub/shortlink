package com.project.remote.dto.req;

import lombok.Data;

/**
 * 从回收站恢复短链接请求实体
 */
@Data
public class ShortLinkRecoverReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
