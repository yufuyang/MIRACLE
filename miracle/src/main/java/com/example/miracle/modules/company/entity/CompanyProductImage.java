package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公司产品图片实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_product_image")
public class CompanyProductImage extends BaseEntity {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 是否主图：0-否 1-是
     */
    private Integer isMain;
} 