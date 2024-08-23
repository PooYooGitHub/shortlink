package com.project.controller;

import com.project.common.convention.result.Result;
import com.project.common.convention.result.Results;
import com.project.service.UrlTitleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UrlTitleController {
    private final UrlTitleService urlTitleService;

    /**
     * 根据原始链接查询网站标题
     */
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getOriginLinkTile(@RequestParam("url") String url){
        return Results.success(urlTitleService.getOriginLinkTile(url));
    }
}
