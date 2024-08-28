package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkOsStatsDO;
import com.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据短链接获取指定日期内操作系统监控数据
     */
    @Select("SELECT " +
            "    os, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_os_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, date, os;")
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
