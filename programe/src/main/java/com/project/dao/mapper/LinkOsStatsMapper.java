package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {
    @Insert("""
            INSERT INTO t_link_os_stats (
              full_short_url,
              gid,
              date,
                cnt,
              os,
              create_time,
              update_time,
              del_flag
            ) VALUES (
              #{linkOsStatsDO.fullShortUrl},
              #{linkOsStatsDO.gid},
              #{linkOsStatsDO.date},
                #{linkOsStatsDO.cnt},
              #{linkOsStatsDO.os},
              NOW(),
              NOW(),
              #{linkOsStatsDO.delFlag}
            ) ON DUPLICATE KEY UPDATE
              cnt = #{linkOsStatsDO.cnt} + cnt
              ;""")
    void insertOrUpdate(@Param("linkOsStatsDO") LinkOsStatsDO linkOsStatsDO);

}
