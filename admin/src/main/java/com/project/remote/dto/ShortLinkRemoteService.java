package com.project.remote.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.common.convention.result.Result;
import com.project.remote.dto.req.*;
import com.project.remote.dto.resp.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程调用短链接服务
 */

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
        hashMap.put("orderTag", requestParam.getOrderTag());
        String resp = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", hashMap);
        return JSON.parseObject(resp, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });
    }

    /**
     * 查询分组下短链接的数量
     * @param gids 需要查询的分组标识gid
     * @return
     */
    default Result<List<GroupShortLinkCountRespDTO>> countShortLinkInGroup(List<String> gids) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("gid", gids);
        String resp = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", hashMap);
        return JSON.parseObject(resp, new TypeReference<Result<List<GroupShortLinkCountRespDTO>>>() {
        });
    }
    /**
     * 访问单个短链接指定时间内监控数据(图表显示)
     *
     * @param requestParam 访问短链接监控请求参数
     * @return 短链接监控信息
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }


    /**
     * 访问单个短链接指定时间内监控数据(历史访问记录)
     * @param requestParam 访问短链接监控请求参数
     * @return 短链接监控信息
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>>  shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(requestParam, false, true);
        stringObjectMap.remove("orders");
        stringObjectMap.remove("records");
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record", stringObjectMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<Result<IPage<ShortLinkStatsAccessRecordRespDTO>>>() {
        });
    }
    /**
     * 访问分组短链接指定时间内监控数据
     *
     * @param requestParam 访分组问短链接监控请求参数
     * @return 分组短链接监控信息
     */
    default Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/group", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

}
