package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.remote.dto.UrlTitleRemoteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UrlTitleController {
    private final UrlTitleRemoteService urlTitleRemoteService=new UrlTitleRemoteService() {
    };

    /**
     * 根据原始链接查询网站标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getOriginLinkTile(@RequestParam("url") String url){
        return urlTitleRemoteService.getOriginLinkTile(url);
    }
}
