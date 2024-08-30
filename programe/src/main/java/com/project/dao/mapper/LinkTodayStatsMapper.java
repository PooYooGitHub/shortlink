package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkTodayStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkTodayStatsMapper extends BaseMapper<LinkTodayStatsDO> {
    @Insert("""
            INSERT INTO t_link_stats_today (
              full_short_url,
              gid,
              date,
              today_pv,
                                today_uv,
                                today_uip,
              create_time,
              update_time,
              del_flag
            ) VALUES (
              #{linkTodayStatsDO.fullShortUrl},
              #{linkTodayStatsDO.gid},
              #{linkTodayStatsDO.date},
            #{linkTodayStatsDO.todayPv},
                                #{linkTodayStatsDO.todayUv},
                                #{linkTodayStatsDO.todayUip},
              NOW(),
              NOW(),
              #{linkTodayStatsDO.delFlag}
            ) ON DUPLICATE KEY UPDATE
              today_pv = #{linkTodayStatsDO.todayPv} + today_pv,
                                today_uv = #{linkTodayStatsDO.todayUv} + today_uv,
                                today_uip = #{linkTodayStatsDO.todayUip} + today_uip
              ;""")
    public void insertOrUpdate(@Param("linkTodayStatsDO") LinkTodayStatsDO linkTodayStatsDO);
}
