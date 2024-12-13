package com.example.miracle.modules.company.dto.query;

import com.example.miracle.common.dto.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公司产品分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyProductPageQuery extends PageQuery {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编号
     */
    private String productCode;

    /**
     * 产品类别
     */
    private String category;

    /**
     * 状态
     */
    private Integer status;
} 