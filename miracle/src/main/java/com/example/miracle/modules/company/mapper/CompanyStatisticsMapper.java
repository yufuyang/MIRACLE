package com.example.miracle.modules.company.mapper;

import com.example.miracle.modules.company.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CompanyStatisticsMapper {

    @Select("SELECT COUNT(DISTINCT m.id) FROM merchant m " +
            "INNER JOIN orders o ON m.id = o.merchant_id " +
            "WHERE m.company_id = #{companyId} " +
            "AND o.create_time >= #{startTime}")
    Integer getActiveMerchantCount(@Param("companyId") Long companyId,
                                   @Param("startTime") LocalDateTime startTime);

    @Select("SELECT m.id as merchant_id, m.merchant_name, " +
            "SUM(o.total_amount) as sales_amount, COUNT(o.id) as sales_count " +
            "FROM merchant m " +
            "INNER JOIN orders o ON m.id = o.merchant_id " +
            "WHERE m.company_id = #{companyId} " +
            "AND o.create_time >= #{startTime} " +
            "GROUP BY m.id, m.merchant_name " +
            "ORDER BY sales_amount DESC " +
            "LIMIT #{limit}")
    List<MerchantRankDTO> getTopMerchants(@Param("companyId") Long companyId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("limit") Integer limit);

    @Select("SELECT p.id as product_id, p.product_name, " +
            "SUM(od.quantity) as sales_count, " +
            "SUM(od.quantity * od.price) as sales_amount " +
            "FROM product p " +
            "INNER JOIN order_detail od ON p.id = od.product_id " +
            "INNER JOIN orders o ON od.order_id = o.id " +
            "INNER JOIN merchant m ON o.merchant_id = m.id " +
            "WHERE m.company_id = #{companyId} " +
            "AND o.create_time >= #{startTime} " +
            "GROUP BY p.id, p.product_name " +
            "ORDER BY sales_count DESC " +
            "LIMIT #{limit}")
    List<ProductRankDTO> getTopProducts(@Param("companyId") Long companyId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("limit") Integer limit);


    /**
     * 获取每日商户数量
     */
    @Select("SELECT COUNT(*) FROM merchant " +
            "WHERE company_id = #{companyId} " +
            "AND DATE(create_time) <= #{date} " +
            "AND (status = 1 OR status = 2)")
    Integer getDailyMerchantCount(@Param("companyId") Long companyId,
                                  @Param("date") LocalDate date);

    /**
     * 获取每日销售额
     */
    @Select("SELECT COALESCE(SUM(o.total_amount), 0) " +
            "FROM orders o " +
            "INNER JOIN merchant m ON o.merchant_id = m.id " +
            "WHERE m.company_id = #{companyId} " +
            "AND DATE(o.create_time) = #{date} " +
            "AND o.status = 3")  // 假设状态3为已完成订单
    BigDecimal getDailySalesAmount(@Param("companyId") Long companyId,
                                   @Param("date") LocalDate date);

    /**
     * 获取每日订单数
     */
    @Select("SELECT COUNT(*) " +
            "FROM orders o " +
            "INNER JOIN merchant m ON o.merchant_id = m.id " +
            "WHERE m.company_id = #{companyId} " +
            "AND DATE(o.create_time) = #{date}")
    Integer getDailyOrderCount(@Param("companyId") Long companyId,
                               @Param("date") LocalDate date);

    /**
     * 获取商户分类统计
     */
    @Select("SELECT m.category, COUNT(*) as count " +
            "FROM merchant m " +
            "WHERE m.company_id = #{companyId} " +
            "AND (m.status = 1 OR m.status = 2) " +
            "GROUP BY m.category")
    List<CategoryStatDTO> getMerchantCategoryStats(@Param("companyId") Long companyId);

    /**
     * 获取商户区域统计
     */
    @Select("SELECT m.area, COUNT(*) as count " +
            "FROM merchant m " +
            "WHERE m.company_id = #{companyId} " +
            "AND (m.status = 1 OR m.status = 2) " +
            "GROUP BY m.area")
    List<AreaStatDTO> getMerchantAreaStats(@Param("companyId") Long companyId);

    /**
     * 获取时段销售统计
     */
    @Select("SELECT HOUR(o.create_time) as hour, " +
            "COUNT(*) as order_count, " +
            "SUM(o.total_amount) as sales_amount " +
            "FROM orders o " +
            "INNER JOIN merchant m ON o.merchant_id = m.id " +
            "WHERE m.company_id = #{companyId} " +
            "AND DATE(o.create_time) = #{date} " +
            "AND o.status = 3 " +
            "GROUP BY HOUR(o.create_time) " +
            "ORDER BY hour")
    List<HourlyStatDTO> getHourlySalesStats(@Param("companyId") Long companyId,
                                            @Param("date") LocalDate date);

    /**
     * 获取商户销售额区间统计
     */
    @Select("WITH merchant_sales AS (" +
            "  SELECT m.id, COALESCE(SUM(o.total_amount), 0) as sales_amount " +
            "  FROM merchant m " +
            "  LEFT JOIN orders o ON m.id = o.merchant_id " +
            "    AND o.create_time >= #{startTime} " +
            "    AND o.status = 3 " +
            "  WHERE m.company_id = #{companyId} " +
            "  GROUP BY m.id" +
            ") " +
            "SELECT " +
            "  CASE " +
            "    WHEN sales_amount = 0 THEN '无销售' " +
            "    WHEN sales_amount < 10000 THEN '1万以下' " +
            "    WHEN sales_amount < 50000 THEN '1-5万' " +
            "    WHEN sales_amount < 100000 THEN '5-10万' " +
            "    ELSE '10万以上' " +
            "  END as range, " +
            "  COUNT(*) as count " +
            "FROM merchant_sales " +
            "GROUP BY " +
            "  CASE " +
            "    WHEN sales_amount = 0 THEN '无销售' " +
            "    WHEN sales_amount < 10000 THEN '1万以下' " +
            "    WHEN sales_amount < 50000 THEN '1-5万' " +
            "    WHEN sales_amount < 100000 THEN '5-10万' " +
            "    ELSE '10万以上' " +
            "  END " +
            "ORDER BY " +
            "  CASE range " +
            "    WHEN '无销售' THEN 1 " +
            "    WHEN '1万以下' THEN 2 " +
            "    WHEN '1-5万' THEN 3 " +
            "    WHEN '5-10万' THEN 4 " +
            "    ELSE 5 " +
            "  END")
    List<SalesRangeStatDTO> getMerchantSalesRangeStats(@Param("companyId") Long companyId,
                                                      @Param("startTime") LocalDateTime startTime);


    /**
     * 获取待审核商户数
     */
    @Select("SELECT COUNT(*) FROM merchant " +
            "WHERE company_id = #{companyId} " +
            "AND status = 0")
    Integer getPendingMerchantCount(@Param("companyId") Long companyId);

    /**
     * 获取新增商户数
     */
    @Select("SELECT COUNT(*) FROM merchant " +
            "WHERE company_id = #{companyId} " +
            "AND create_time >= #{startTime}")
    Integer getNewMerchantCount(@Param("companyId") Long companyId,
                                @Param("startTime") LocalDateTime startTime);

}