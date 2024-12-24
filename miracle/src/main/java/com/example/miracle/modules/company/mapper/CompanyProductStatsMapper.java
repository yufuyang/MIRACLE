package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.website.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
}