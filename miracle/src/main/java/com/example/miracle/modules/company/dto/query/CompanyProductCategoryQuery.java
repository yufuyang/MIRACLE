package com.example.miracle.modules.company.dto.query;

import lombok.Data;

/**
 * 公司产品分类查询对象
 */
@Data
public class CompanyProductCategoryQuery {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 状态
     */
    private Integer status;
} 