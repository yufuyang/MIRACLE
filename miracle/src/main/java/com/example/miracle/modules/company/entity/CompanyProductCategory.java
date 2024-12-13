package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公司产品分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_product_category")
public class CompanyProductCategory extends BaseEntity {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
} 