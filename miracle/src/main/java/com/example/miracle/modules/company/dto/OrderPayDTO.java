package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderPayDTO {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付方式：1-微信 2-支付宝 3-现金
     */
    private Integer payType;

    /**
     * 支付金额（现金支付时必填）
     */
    private BigDecimal payAmount;

    /**
     * 操作人
     */
    private String operator;
}
