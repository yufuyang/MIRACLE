package com.example.miracle.modules.company.dto.query;

import com.example.miracle.common.dto.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品统计查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyProductStatsQuery extends PageQuery {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 是否升序
     */
    private Boolean asc;
} 