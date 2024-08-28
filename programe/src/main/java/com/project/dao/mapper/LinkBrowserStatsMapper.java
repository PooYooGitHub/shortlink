package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {
    @Insert("""
            INSERT INTO t_link_browser_stats (
              full_short_url,
              gid,
              date,
                cnt,
              browser,
              create_time,
              update_time,
              del_flag
            ) VALUES (
              #{linkBrowserStatsDO.fullShortUrl},
              #{linkBrowserStatsDO.gid},
              #{linkBrowserStatsDO.date},
                #{linkBrowserStatsDO.cnt},
              #{linkBrowserStatsDO.browser},
              NOW(),
              NOW(),
              #{linkBrowserStatsDO.delFlag}
            ) ON DUPLICATE KEY UPDATE
              cnt = #{linkBrowserStatsDO.cnt} + cnt
              ;""")
    void insertOrUpdate(@Param("linkBrowserStatsDO") LinkBrowserStatsDO linkBrowserStatsDO);

}
