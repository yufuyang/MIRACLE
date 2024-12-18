package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CompanyProductStatsMapper extends BaseMapper<CompanyProductStats> {

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

}