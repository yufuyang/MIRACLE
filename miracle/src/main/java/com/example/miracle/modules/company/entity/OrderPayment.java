package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_payment")
@EqualsAndHashCode(callSuper = true)
public class OrderPayment extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付方式：1-微信 2-支付宝
     */
    private Integer payType;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付流水号
     */
    private String tradeNo;

    /**
     * 支付状态：0-未支付 1-支付中 2-支付成功 3-支付失败 4-已关闭
     */
    private Integer payStatus;

    /**
     * 失败原因
     */
    private String failReason;

    private LocalDateTime payTime;
}
