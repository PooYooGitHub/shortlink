package com.project.util;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 短链接工具类
 */

public class LinkUtil {
    public static final long ONE_WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 获取短链接缓存过期时间戳
     *
     * @param date 日期
     * @return 短链接缓存过期时间戳
     */
    public static long getShortLinkCacheTime(Date date) {
        // 当前时间
        long currentTimeMillis = System.currentTimeMillis();

        // 如果 date 为 null（永久有效），则默认设置为一周后的时间戳，并随机一个 0 ~ 24 小时的时间
        if (date == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            return calendar.getTimeInMillis() + ThreadLocalRandom.current().nextLong(0, 24 * 60 * 60 * 1000L)-currentTimeMillis;
        }

        long dateMillis = date.getTime();

        // 检查 date 是否超过一周
        if (dateMillis - currentTimeMillis > ONE_WEEK_MILLIS) {
            // 如果超过一周，那么将有效时间设置在一周后
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            return calendar.getTimeInMillis() + ThreadLocalRandom.current().nextLong(0, 24 * 60 * 60 * 1000L)-currentTimeMillis;
        } else {
            // 如果没有超过一周，则设置为 date 的时间戳
            return dateMillis-currentTimeMillis;
        }
    }
    /**
     * 获取请求的 IP 地址
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }
    /**
     * 获取用户访问操作系统
     *
     * @param request 请求
     * @return 访问操作系统
     */
    public static String getOs(HttpServletRequest request) {
        UserAgent parse = UserAgentUtil.parse(request.getHeader("User-Agent"));
        return parse.getOs().getName();
    }
    /**
     * 获取用户访问浏览器
     *
     * @param request 请求
     * @return 访问操作系统
     */
    public static String getBrowser(HttpServletRequest request) {
        UserAgent parse = UserAgentUtil.parse(request.getHeader("User-Agent"));
        return parse.getBrowser().getName();
    }
}
