package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@TableName("return_item")
@EqualsAndHashCode(callSuper = true)
public class ReturnItem extends BaseEntity {

    /**
     * 退货单ID
     */
    private Long returnId;

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 退货数量
     */
    private Integer quantity;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 退货金额
     */
    private BigDecimal totalAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退货原因
     */
    private String reason;
}