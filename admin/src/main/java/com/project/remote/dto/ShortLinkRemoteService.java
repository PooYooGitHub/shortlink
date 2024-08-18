package com.project.remote.dto;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.remote.dto.req.ShortLinkCreateReqDTO;
import com.project.remote.dto.req.ShortLinkPageReqDTO;
import com.project.remote.dto.resp.ShortLinkCreateRespDTO;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;

public interface ShortLinkRemoteService {
    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        //使用http的方法来远程调用
        String resp = HttpUtil.
                post("http://127.0.0.1:8001/api/short-link/v1/create", JSONUtil.toJsonStr(requestParam));
        return JSON.parseObject(resp, new TypeReference<Result<ShortLinkCreateRespDTO>>() {
        });

    }

    /**
     * 分页查询短链接
     * @param requestParam
     * @return
     */
    default Result<IPage<ShortLinkPageRespDTO>> queryPage(ShortLinkPageReqDTO requestParam) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gid", requestParam.getGid());
        hashMap.put("current", requestParam.getCurrent());
        hashMap.put("size", requestParam.getSize());
        String resp = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", hashMap);
        return JSON.parseObject(resp, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });
    }

}
