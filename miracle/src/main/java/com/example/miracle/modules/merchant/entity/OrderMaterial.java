package com.example.miracle.modules.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_material")
public class OrderMaterial extends BaseEntity {
    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 物料ID
     */
    private Long materialId;

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
