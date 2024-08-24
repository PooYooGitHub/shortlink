package com.project.remote.dto;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.project.remote.dto.req.ShortLinkToRecycleBinReqDTO;

/**
 * 远程调用回收站服务
 */
public interface RecycleBinRemoteService {
    /**
     * 将短链接移至回收站
     * @param requestParam 请求实体
     * @return
     */
    default void moveShortLinkToRecycleBin(ShortLinkToRecycleBinReqDTO requestParam){
        String resp = HttpUtil.
                post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSONUtil.toJsonStr(requestParam));
        return ;
    }
}
