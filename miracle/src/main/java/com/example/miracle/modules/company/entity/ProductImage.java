package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("product_image")
@EqualsAndHashCode(callSuper = true)
public class ProductImage extends BaseEntity {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 图片URL
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