package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkLocateStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkLocateStatsMapper extends BaseMapper<LinkLocateStatsDO> {
    @Insert("""
            INSERT INTO t_link_locate_stats (
              full_short_url,
              gid,
              date,
              cnt,
                province,
                city,
                adcode,
                country,
              create_time,
              update_time,
              del_flag
            ) VALUES (
              #{linkLocateStatsDO.fullShortUrl},
              #{linkLocateStatsDO.gid},
              #{linkLocateStatsDO.date},
              #{linkLocateStatsDO.cnt},
              #{linkLocateStatsDO.province},
              #{linkLocateStatsDO.city},
              #{linkLocateStatsDO.adcode},
              #{linkLocateStatsDO.country},
              NOW(),
              NOW(),
              #{linkLocateStatsDO.delFlag}
            ) ON DUPLICATE KEY UPDATE
              cnt = #{linkLocateStatsDO.cnt} + cnt
              ;""")
    void insertOrUpdate(@Param("linkLocateStatsDO") LinkLocateStatsDO linkLocateStatsDO);

}
