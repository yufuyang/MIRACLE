package com.example.miracle.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.admin.entity.RankData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RankDataMapper extends BaseMapper<RankData> {

    /**
     * 获取商户销售排行数据
     */
    @Select("SELECT m.id as target_id, m.merchant_name as target_name, " +
            "COALESCE(SUM(o.total_amount), 0) as rank_value " +
            "FROM merchant m " +
            "LEFT JOIN orders o ON m.id = o.merchant_id " +
            "WHERE o.create_time >= #{startTime} AND o.create_time < #{endTime} " +
            "GROUP BY m.id, m.merchant_name " +
            "ORDER BY rank_value DESC")
    List<RankData> getMerchantSalesRank(@Param("startTime") LocalDate startTime,
                                        @Param("endTime") LocalDate endTime);

    /**
     * 获取商品销售排行数据
     */
    @Select("SELECT p.id as target_id, p.product_name as target_name, " +
            "COALESCE(SUM(od.quantity), 0) as rank_value " +
            "FROM product p " +
            "LEFT JOIN order_detail od ON p.id = od.product_id " +
            "LEFT JOIN orders o ON od.order_id = o.id " +
            "WHERE o.create_time >= #{startTime} AND o.create_time < #{endTime} " +
            "GROUP BY p.id, p.product_name " +
            "ORDER BY rank_value DESC")
    List<RankData> getProductSalesRank(@Param("startTime") LocalDate startTime,
                                       @Param("endTime") LocalDate endTime);
}