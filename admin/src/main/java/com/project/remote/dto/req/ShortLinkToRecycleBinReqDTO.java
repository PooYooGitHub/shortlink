package com.project.remote.dto.req;

import lombok.Data;

/**
 * 将短链接移至回收站请求实体
 */

@Data
public class ShortLinkToRecycleBinReqDTO {
    /**
     * 短链接分组标识
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
