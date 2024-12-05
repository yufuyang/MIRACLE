package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order_payment")
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
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付方式ID
     */
    private Long payMethodId;

    /**
     * 支付方式类型：1-微信 2-支付宝
     */
    private Integer payType;

    /**
     * 支付方式名称
     */
    private String payMethodName;

    /**
     * 支付状态：0-待支付 1-支付成功 2-支付失败
     */
    private Integer payStatus;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付流水号
     */
    private String tradeNo;

    /**
     * 支付失败原因
     */
    private String failReason;

    /**
     * 支付配置ID
     */
    private Long configId;

    /**
     * 支付配置信息
     */
    private String config;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount = BigDecimal.ZERO;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款状态：0-未退款 1-部分退款 2-全额退款
     */
    private Integer refundStatus;
}
