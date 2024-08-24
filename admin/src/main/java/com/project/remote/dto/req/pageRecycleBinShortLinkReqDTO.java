package com.project.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class pageRecycleBinShortLinkReqDTO extends Page {
    /**
     * 分组列表
     */
    private List<String> gids;

}
