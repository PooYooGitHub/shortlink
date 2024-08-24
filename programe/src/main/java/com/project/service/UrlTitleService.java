package com.project.service;

/**
 * 获取网站标题接口层
 */
public interface UrlTitleService  {

    /**
     * 获取网站标题
     * @param url 网站链接
     * @return
     */
    String getOriginLinkTile(String url);
}
