package com.project.dto.resp;

import lombok.Data;

/**
 * 分组短链接数量响应实体
 */
@Data
public class GroupShortLinkCountRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接数量
     */
    private Integer shortLinkCount;


}
