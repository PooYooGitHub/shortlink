package com.project.remote.dto;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.remote.dto.req.ShortLinkRecoverReqDTO;
import com.project.remote.dto.req.ShortLinkToRecycleBinReqDTO;
import com.project.remote.dto.req.pageRecycleBinShortLinkReqDTO;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;

/**
 * 远程调用回收站服务
 */

public interface RecycleBinRemoteService {


    /**
     * 将短链接移至回收站
     *
     * @param requestParam 请求实体
     * @return
     */
    default void moveShortLinkToRecycleBin(ShortLinkToRecycleBinReqDTO requestParam) {
        String resp = HttpUtil.
                post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSONUtil.toJsonStr(requestParam));
        return;
    }

    /**
     * 分页查询用户回收站的短链接
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(pageRecycleBinShortLinkReqDTO requestParam) {
        //查询当前用户下的所有分组标识
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gids", requestParam.getGids());
        hashMap.put("current", requestParam.getCurrent());
        hashMap.put("size", requestParam.getSize());
        String resp = HttpRequest.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page")
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(hashMap))
                .execute()
                .body();
        return JSON.parseObject(resp, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });

    }

    /**
     * 从回收站恢复短链接
     * @param requestParam
     */

    default public void recoverShortLink(ShortLinkRecoverReqDTO requestParam) {
        String post = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover", JSONUtil.toJsonStr(requestParam));
        return;

    }

}