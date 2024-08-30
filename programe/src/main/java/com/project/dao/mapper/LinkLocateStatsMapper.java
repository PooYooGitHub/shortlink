package com.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.dao.entity.LinkLocateStatsDO;
import com.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locate_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, province;")
    List<LinkLocateStatsDO> listLocateByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内地区监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locate_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, province;")
    List<LinkLocateStatsDO> listLocaleByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
