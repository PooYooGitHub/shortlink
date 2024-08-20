package com.project.remote.dto.resp;

import lombok.Data;

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
