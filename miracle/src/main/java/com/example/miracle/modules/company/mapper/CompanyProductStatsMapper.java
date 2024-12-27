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
    @Select("SELECT COALESCE(SUM(view_count), 0) as views, " +
            "COALESCE(SUM(intention_count), 0) as intentions " +
            "FROM company_product_stats " +
            "WHERE company_id = #{companyId} " +
            "AND stats_date = CURRENT_DATE")
    Map<String, Integer> getTodayStats(@Param("companyId") Long companyId);

    /**
     * 获取企业总计统计数据
     */
    @Select("SELECT COALESCE(SUM(view_count), 0) as views, " +
            "COALESCE(SUM(intention_count), 0) as intentions " +
            "FROM company_product_stats " +
            "WHERE company_id = #{companyId}")
    Map<String, Integer> getTotalStats(@Param("companyId") Long companyId);

    /**
     * 更新产品统计数据
     */
    @Update("INSERT INTO company_product_stats (company_id, product_id, stats_date, view_count, intention_count) " +
            "VALUES (#{companyId}, #{productId}, #{statsDate}, #{viewCount}, #{intentionCount}) " +
            "ON DUPLICATE KEY UPDATE " +
            "view_count = view_count + #{viewCount}, " +
            "intention_count = intention_count + #{intentionCount}")
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
    @Update("UPDATE company_product_stats SET view_count = view_count + 1 WHERE product_id = #{productId}")
    void incrementViewCount(@Param("productId") Long productId);

    /**
     * 增加意向数
     */
    @Update("UPDATE company_product_stats SET intent_count = intent_count + 1 WHERE product_id = #{productId}")
    void incrementIntentCount(@Param("productId") Long productId);

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
            "s.view_count as viewCount, " +
            "s.intention_count as intentionCount " +
            "FROM company_product_stats s " +
            "INNER JOIN company_product p ON s.product_id = p.id " +
            "WHERE p.status = 1 " +
            "ORDER BY s.intention_count DESC, s.view_count DESC " +
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
            "s.view_count as viewCount, " +
            "s.intention_count as intentionCount " +
            "FROM company_product_stats s " +
            "INNER JOIN company_product p ON s.product_id = p.id " +
            "WHERE p.status = 1 AND s.company_id = #{companyId} " +
            "ORDER BY " +
            "CASE WHEN #{rankType} = 'views' THEN s.view_count " +
            "     WHEN #{rankType} = 'intentions' THEN s.intention_count " +
            "END DESC " )
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