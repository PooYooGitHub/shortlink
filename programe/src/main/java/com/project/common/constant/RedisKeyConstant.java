package com.project.common.constant;

/**
 * Redis Key 常量类
 */
public class RedisKeyConstant {
    /**
     * 短链接跳转key
     */
    public static final String GO_TO_SHORT_LINK_KEY="short-link_go_to:%s";
    /**
     * 短链接跳转空值key
     */
    public static final String GO_TO_IS_NULL_SHORT_LINK_KEY="short-link_is-null_go_to:%s";
    /**
     * 短链接跳转锁key
     */
    public static final String LOCK_GO_TO_SHORT_LINK_KEY="short-link_lock_go_to:%s";

    /**
     * 短链接uip统计key
     */
    public static final String SHORT_LINK_UIP_STATS_KEY="short-link:stats:uip:";
}
