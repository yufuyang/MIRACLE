package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.entity.ActivityStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
@Mapper
public interface ActivityStatsMapper extends BaseMapper<ActivityStats> {

    /**
     * 获取活动统计概览
     */
    @Select("SELECT " +
            "COUNT(DISTINCT a.id) as totalCount, " +
            "COUNT(DISTINCT CASE WHEN a.status = 1 THEN a.id END) as activeCount, " +
            "COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "COALESCE(SUM(s.register_count), 0) as registerCount " +
            "FROM activity a " +
            "LEFT JOIN activity_stats s ON a.id = s.activity_id " +
            "WHERE a.company_id = #{companyId} "
            )
    Map<String, Object> selectStatsOverview(@Param("companyId") Long companyId);

    /**
     * 获取活动趋势数据
     */
    @Select("SELECT DATE_FORMAT(s.stats_date, '%m-%d') as date, " +
            "COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "COALESCE(SUM(s.register_count), 0) as registerCount " +
            "FROM activity_stats s " +
            "LEFT JOIN activity a ON s.activity_id = a.id " +
            "WHERE a.company_id = #{companyId} " +
            "AND s.stats_date BETWEEN DATE_SUB(CURDATE(), INTERVAL #{days} DAY) AND CURDATE() " +
            "GROUP BY s.stats_date")
    List<Map<String, Object>> selectStatsTrend(@Param("companyId") Long companyId, @Param("activityId") Long activityId, @Param("days") Integer days);

    /**
     * 获取活动选项列表
     */
    @Select("SELECT id, title " +
            "FROM activity " +
            "WHERE company_id = #{companyId} " +
            "ORDER BY created_at DESC")
    List<Map<String, Object>> selectActivityOptions(@Param("companyId") Long companyId);

    /**
     * 获取热门活动排行
     */
    @Select("<script>" +
            "SELECT " +
            "    a.id, " +
            "    a.title, " +
            "    a.description, " +
            "    a.start_time, " +
            "    a.end_time, " +
            "    a.status, " +
            "    a.company_id, " +
            "    a.create_Time, " +
            "    a.update_Time, " +
            "    COALESCE(SUM(s.view_count), 0) as view_count, " +
            "    COALESCE(SUM(s.register_count), 0) as register_count " +
            "FROM activity a " +
            "LEFT JOIN activity_stats s ON a.id = s.activity_id " +
            "WHERE a.company_id = #{companyId} " +
            "GROUP BY a.id " +
            "<if test='type == \"view\"'>" +
            "ORDER BY view_count DESC, a.create_Time DESC " +
            "</if>" +
            "<if test='type == \"register\"'>" +
            "ORDER BY register_count DESC, a.create_Time DESC " +
            "</if>" +
            "LIMIT 10" +
            "</script>")
    List<ActivityDTO> selectHotActivities(@Param("companyId") Long companyId, @Param("type") String type);

    @Select("SELECT * FROM activity_stats " +
            "WHERE activity_id = #{activityId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate}")
    List<ActivityStats> getStatsByDateRange(@Param("activityId") Long activityId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
} 