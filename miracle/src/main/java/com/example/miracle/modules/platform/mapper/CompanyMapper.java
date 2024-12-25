package com.example.miracle.modules.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.modules.platform.dto.query.CompanyPageQuery;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.website.dto.CompanyDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 公司信息Mapper接口
 */
public interface CompanyMapper extends BaseMapper<Company> {

    /**
     * 分页查询公司DTO
     */
    @Select({
            "<script>",
            "SELECT t.id, t.company_name, t.company_desc, t.logo_url, t.status, t.create_time,",
            "       t.product_count, t.intention_count",
            "FROM (",
            "    SELECT c.id, c.company_name, c.company_desc, c.logo_url, c.status, c.create_time,",
            "           COALESCE(temp.product_count, 0) as product_count,",
            "           COALESCE(temp.intention_count, 0) as intention_count",
            "    FROM company c",
            "    LEFT JOIN (",
            "        SELECT p.company_id as cid,",
            "               COUNT(DISTINCT p.id) as product_count,",
            "               SUM(COALESCE(s.intention_count, 0)) as intention_count",
            "        FROM company_product p",
            "        LEFT JOIN company_product_stats s ON p.id = s.product_id",
            "        WHERE p.status = 1",
            "        GROUP BY p.company_id",
            "    ) temp ON c.id = temp.cid",
            ") t",
            "WHERE 1 = 1",
            "<if test='query.companyName != null and query.companyName != \"\"'>",
            "    AND t.company_name LIKE CONCAT('%', #{query.companyName}, '%')",
            "</if>",
            "<if test='query.contactName != null and query.contactName != \"\"'>",
            "    AND t.contact_name LIKE CONCAT('%', #{query.contactName}, '%')",
            "</if>",
            "<if test='query.status != null'>",
            "    AND t.status = #{query.status}",
            "</if>",
            "<choose>",
            "    <when test='query.orderField == \"productCount\"'>",
            "        ORDER BY t.product_count <if test='query.asc'>ASC</if><if test='!query.asc'>DESC</if>",
            "    </when>",
            "    <when test='query.orderField == \"intentionCount\"'>",
            "        ORDER BY t.intention_count <if test='query.asc'>ASC</if><if test='!query.asc'>DESC</if>",
            "    </when>",
            "    <otherwise>",
            "        ORDER BY t.create_time DESC",
            "    </otherwise>",
            "</choose>",
            "</script>"
    })
    Page<CompanyDTO> selectCompanyDTOPage(@Param("page") Page<CompanyDTO> page, @Param("query") CompanyPageQuery query);
}