package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.website.dto.ProductDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 公司产品 Mapper 接口
 */
public interface CompanyProductMapper extends BaseMapper<CompanyProduct> {

    /**
     * 分页查询产品DTO
     */
    @Select({
            "<script>",
            "SELECT",
            "    p.id,",
            "    p.product_name,",
            "    p.description,",
            "    p.image_url,",
            "    COALESCE(s.view_count, 0) as view_count,",
            "    COALESCE(s.intention_count, 0) as intention_count",
            "FROM company_product p",
            "LEFT JOIN company_product_stats s ON p.id = s.product_id",
            "<where>",
            "    <if test='query.productName != null and query.productName != \"\"'>",
            "        AND p.product_name LIKE CONCAT('%', #{query.productName}, '%')",
            "    </if>",
            "    AND p.status = 1",
            "</where>",
            "<choose>",
            "    <when test='query.orderField == \"viewCount\"'>",
            "        ORDER BY s.view_count <if test='query.asc'>ASC</if><if test='!query.asc'>DESC</if>",
            "    </when>",
            "    <when test='query.orderField == \"intentionCount\"'>",
            "        ORDER BY s.intention_count <if test='query.asc'>ASC</if><if test='!query.asc'>DESC</if>",
            "    </when>",
            "    <otherwise>",
            "        ORDER BY p.create_time DESC",
            "    </otherwise>",
            "</choose>",
            "</script>"
    })
    Page<ProductDTO> selectProductDTOPage(@Param("page") Page<ProductDTO> page, @Param("query") CompanyProductPageQuery query);
} 