package com.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService {
    /**
     * 分页查询回收站短链接
     *
     * @param  current 当前页,size 每页显示条数
     * @return 返回参数包装
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(String current, String size);
}
