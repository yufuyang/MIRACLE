package com.example.miracle.modules.merchant.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderMaterialDTO {

    private Long id;
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 物料ID
     */
    private Long materialId;
    /**
     * 物料名称
     */
    private String materialName;

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
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 总金额
     */
    private BigDecimal amount;
}
