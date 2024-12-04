package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderPayCallbackDTO {
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
}
