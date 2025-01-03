package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.website.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CompanyProductStatsMapper extends BaseMapper<CompanyProductStats> {

    /**
     * 获取指定日期范围内的产品统计数据
     */
    @Select("SELECT * FROM company_product_stats WHERE product_id = #{productId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate}")
    List<CompanyProductStats> getStatsByDateRange(@Param("productId") Long productId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取企业今日统计数据
     */
    @Select("SELECT CAST(COALESCE(SUM(view_count), 0) AS SIGNED) as views, " +
            "CAST(COALESCE(SUM(intention_count), 0) AS SIGNED) as intentions " +
            "FROM company_product_stats " +
            "WHERE company_id = #{companyId} " +
            "AND stats_date = CURRENT_DATE")
    Map<String, Long> getTodayStats(@Param("companyId") Long companyId);

    /**
     * 获取企业总计统计数据
     */
    @Select("SELECT CAST(COALESCE(SUM(view_count), 0) AS SIGNED) as views, " +
            "CAST(COALESCE(SUM(intention_count), 0) AS SIGNED) as intentions " +
            "FROM company_product_stats " +
            "WHERE company_id = #{companyId}")
    Map<String, Long> getTotalStats(@Param("companyId") Long companyId);

    /**
     * 更新产品统计数据
     */
    @Update("INSERT INTO company_product_stats (company_id, product_id, stats_date, view_count, intention_count, create_time, update_time) " +
            "VALUES (#{companyId}, #{productId}, #{statsDate}, #{viewCount}, #{intentionCount}, " +
            "COALESCE(#{createTime}, NOW()), NOW()) " +
            "ON DUPLICATE KEY UPDATE " +
            "view_count = view_count + #{viewCount}, " +
            "intention_count = intention_count + #{intentionCount}, " +
            "update_time = NOW()")
    int updateStats(CompanyProductStats stats);


    /**
     * 减少意向数
     */
    @Update("UPDATE company_product_stats " +
            "SET intention_count = GREATEST(intention_count - 1, 0) " +
            "WHERE product_id = #{productId} " +
            "AND stats_date = CURRENT_DATE")
    void decrementIntentCount(@Param("companyId") Long companyId);

    /**
     * 增加浏览量
     */
    @Update("INSERT INTO company_product_stats (product_id, company_id, stats_date, view_count, intention_count, create_time, update_time) " +
            "VALUES (#{productId}, #{companyId}, CURRENT_DATE, 1, 0, NOW(), NOW()) " +
            "ON DUPLICATE KEY UPDATE " +
            "view_count = view_count + 1, " +
            "update_time = NOW()")
    void incrementViewCount(@Param("productId") Long productId, @Param("companyId") Long companyId);

    /**
     * 增加意向数
     */
    @Update("INSERT INTO company_product_stats (product_id, company_id, stats_date, view_count, intention_count, create_time, update_time) " +
            "VALUES (#{productId}, #{companyId}, CURRENT_DATE, 0, 1, NOW(), NOW()) " +
            "ON DUPLICATE KEY UPDATE " +
            "intention_count = intention_count + 1, " +
            "update_time = NOW()")
    void incrementIntentCount(@Param("productId") Long productId, @Param("companyId") Long companyId);

    /**
     * 查询前N个优质企业
     * 按产品数量和总意向数排序
     */
    @Select("SELECT company_id, " +
            "COUNT(*) as product_count, " +
            "SUM(intention_count) as total_intention_count, " +
            "SUM(view_count) as total_view_count " +
            "FROM company_product_stats " +
            "GROUP BY company_id " +
            "ORDER BY (SUM(intention_count) + SUM(view_count)) DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectTopCompanies(int limit);

    /**
     * 查询热门产品
     * 联合查询产品信息和统计数据，只返回已上架的产品
     */
    @Select("SELECT " +
            "p.id, " +
            "p.product_name as productName, " +
            "p.description, " +
            "p.image_url as imageUrl, " +
            "COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "COALESCE(SUM(s.intention_count), 0) as intentionCount " +
            "FROM company_product p " +
            "LEFT JOIN company_product_stats s ON s.product_id = p.id " +
            "WHERE p.status = 1 " +
            "GROUP BY p.id, p.product_name, p.description, p.image_url " +
            "ORDER BY intentionCount DESC, viewCount DESC " +
            "LIMIT #{limit}")
    List<ProductDTO> selectHotProducts(@Param("limit") Integer limit);

    /**
     * 获取企业热门产品排行
     */
    @Select("SELECT " +
            "p.id, " +
            "p.product_name as productName, " +
            "p.description, " +
            "p.image_url as imageUrl, " +
            "COALESCE(SUM(s.view_count), 0) as viewCount, " +
            "COALESCE(SUM(s.intention_count), 0) as intentionCount " +
            "FROM company_product p " +
            "LEFT JOIN company_product_stats s ON s.product_id = p.id " +
            "WHERE p.status = 1 AND p.company_id = #{companyId} " +
            "GROUP BY p.id, p.product_name, p.description, p.image_url " +
            "ORDER BY " +
            "CASE WHEN #{rankType} = 'views' THEN SUM(s.view_count) " +
            "     WHEN #{rankType} = 'intentions' THEN SUM(s.intention_count) " +
            "END DESC")
    List<ProductDTO> getCompanyHotProducts(@Param("companyId") Long companyId, @Param("rankType") String rankType);

    /**
     * 获取企业产品的上期数据
     */
    @Select("SELECT product_id, " +
            "SUM(view_count) as lastViews, " +
            "SUM(intention_count) as lastIntentions " +
            "FROM company_product_stats " +
            "WHERE company_id = #{companyId} " +
            "AND stats_date BETWEEN #{lastStartDate} AND #{lastEndDate} " +
            "GROUP BY product_id")
    List<Map<String, Object>> getLastPeriodStats(@Param("companyId") Long companyId, @Param("lastStartDate") LocalDate lastStartDate, @Param("lastEndDate") LocalDate lastEndDate);
}