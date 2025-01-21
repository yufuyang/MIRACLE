package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_material")
public class ProductMaterial extends BaseEntity {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 物料图片
     */
    private String image;

    /**
     * 单位
     */
    private String unit;

    /**
     * 规格
     */
    private String specification;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 首次订购推荐量
     */
    private Integer recommendedQuantity;

    /**
     * 说明
     */
    private String description;
}
