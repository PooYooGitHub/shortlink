package com.project.dto.req;

import lombok.Data;

@Data
public class ShortLinkRemoveReqDTO {
    /**
     * 短链接标识
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
