package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 公司产品实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_product")
public class CompanyProduct extends BaseEntity {

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
     * 产品分类ID
     */
    private Long categoryId;

    /**
     * 产品图片
     */
    private String imageUrl;

    /**
     * 产品价格
     */
    private BigDecimal price;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 状态：0-下架 1-上架
     */
    private Integer status;
} 