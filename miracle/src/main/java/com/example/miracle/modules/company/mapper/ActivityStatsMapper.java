package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.ActivityStats;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ActivityStatsMapper extends BaseMapper<ActivityStats> {

    /**
     * 获取活动统计概览
     */
    @Select("SELECT " +
            "COUNT(DISTINCT a.id) as totalCount, " +
            "COUNT(DISTINCT CASE WHEN a.status = 1 THEN a.id END) as activeCount, " +
            "COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "COALESCE(SUM(s.register_count), 0) as registerCount " +
            "FROM company_activity a " +
            "LEFT JOIN company_activity_stats s ON a.id = s.activity_id " +
            "WHERE a.company_id = #{companyId} " +
            "AND a.deleted = 0")
    Map<String, Object> selectStatsOverview(@Param("companyId") Long companyId);

    /**
     * 获取活动趋势数据
     */
    @Select("<script>" +
            "SELECT DATE_FORMAT(s.stats_date, '%m-%d') as date, " +
            "COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "COALESCE(SUM(s.register_count), 0) as registerCount " +
            "FROM company_activity_stats s " +
            "WHERE s.company_id = #{companyId} " +
            "<if test='activityId != null'>" +
            "AND s.activity_id = #{activityId} " +
            "</if>" +
            "AND s.stats_date BETWEEN DATE_SUB(CURDATE(), INTERVAL #{days} DAY) AND CURDATE() " +
            "GROUP BY s.stats_date " +
            "ORDER BY s.stats_date" +
            "</script>")
    List<Map<String, Object>> selectStatsTrend(@Param("companyId") Long companyId, @Param("activityId") Long activityId, @Param("days") Integer days);

    /**
     * 获取活动选项列表
     */
    @Select("SELECT id, title " +
            "FROM company_activity " +
            "WHERE company_id = #{companyId} " +
            "AND deleted = 0 " +
            "ORDER BY created_at DESC")
    List<Map<String, Object>> selectActivityOptions(@Param("companyId") Long companyId);

    /**
     * 获取热门活动排行
     */
    @Select("<script>" +
            "SELECT " +
            "    a.id, " +
            "    a.title, " +
            "    a.status, " +
            "    COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "    COALESCE(SUM(s.register_count), 0) as registerCount " +
            "FROM company_activity a " +
            "LEFT JOIN company_activity_stats s ON a.id = s.activity_id " +
            "WHERE a.company_id = #{companyId} " +
            "AND a.deleted = 0 " +
            "<if test='type == \"view\"'>" +
            "GROUP BY a.id " +
            "ORDER BY viewCount DESC, a.created_at DESC " +
            "</if>" +
            "<if test='type == \"register\"'>" +
            "GROUP BY a.id " +
            "ORDER BY registerCount DESC, a.created_at DESC " +
            "</if>" +
            "LIMIT 10" +
            "</script>")
    List<Map<String, Object>> selectHotActivities(@Param("companyId") Long companyId, @Param("type") String type);
} 