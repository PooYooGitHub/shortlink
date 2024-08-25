package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    @Insert("INSERT INTO t_link_access_stats (full_short_url, gid, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) " +
            "VALUES (#{linkAccessStatsDO.fullShortUrl}, #{linkAccessStatsDO.gid}, #{linkAccessStatsDO.date}, #{linkAccessStatsDO.pv}, " +
            "#{linkAccessStatsDO.uv}, #{linkAccessStatsDO.uip}, #{linkAccessStatsDO.hour}, #{linkAccessStatsDO.weekday}, NOW(), NOW(), " +
            "#{linkAccessStatsDO.delFlag}) " +
            "ON DUPLICATE KEY UPDATE " +
            "pv = #{linkAccessStatsDO.pv} + VALUES(pv), " +
            "uv = #{linkAccessStatsDO.uv} + VALUES(uv), " +
            "uip = #{linkAccessStatsDO.uip} + VALUES(uip)")
    void insertOrUpdate(@Param("linkAccessStatsDO") LinkAccessStatsDO linkAccessStatsDO);
}
