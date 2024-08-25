package com.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class ShortLinkPageRecycleBinReqDTO extends Page {
    /**
     * 分组列表
     */
    private List<String> gids;

}
