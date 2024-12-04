package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@TableName("product")
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品编码
     */
    private String productCode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销售数量
     */
    private Integer salesCount;

    /**
     * 状态：0-下架 1-上架
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 商品图片列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<ProductImage> images;

    /**
     * 主图URL（非数据库字段）
     */
    @TableField(exist = false)
    private String mainImage;
}