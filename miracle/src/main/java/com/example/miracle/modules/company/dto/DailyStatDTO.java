package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyStatDTO {
    /**
     * 商户数量
     */
    private Integer merchantCount;

    /**
     * 销售额
     */
    private BigDecimal salesAmount;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 平均客单价
     */
    private BigDecimal averageOrderAmount;
}
