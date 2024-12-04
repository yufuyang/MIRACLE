package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class PayResultDTO {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付状态：0-未支付 1-支付中 2-支付成功 3-支付失败 4-已关闭
     */
    private Integer payStatus;

    /**
     * 支付方式：1-微信 2-支付宝 3-银行卡 4-现金
     */
    private Integer payType;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付流水号
     */
    private String tradeNo;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 支付参数（微信支付时返回支付参数）
     */
    private Map<String, Object> payParams;
}
