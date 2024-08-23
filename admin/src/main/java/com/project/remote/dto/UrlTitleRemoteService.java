package com.project.remote.dto;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.project.common.convention.result.Result;

public interface UrlTitleRemoteService {
    default public Result<String> getOriginLinkTile(String url){
        String resp = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title?url="+url);
        return JSON.parseObject(resp,  new TypeReference<Result<String>>(){});
    }
}
