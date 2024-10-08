package com.project.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 短链接创建请求实体
 */
@Data
public class ShortLinkCreateReqDTO {
    /**
     * 创建类型，0：接口创建 1：控制台创建
     */
    private long createdType;
    /**
     * 描述
     */
    private String describe;

    /**
     * 分组
     */
    private String gid;
    /**
     * 原始链接
     */
    private String originUrl;
    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private String validDate;
    /**
     * 有效期类型，0：永久有效 1：自定义
     */
    private long validDateType;
}
