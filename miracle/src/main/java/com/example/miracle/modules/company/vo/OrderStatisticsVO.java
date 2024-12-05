package com.example.miracle.modules.company.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderStatisticsVO {
    
    /**
     * 订单总数
     */
    private Integer totalCount;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 时间维度列表
     */
    private List<String> dateList;
    
    /**
     * 订单数量列表
     */
    private List<Integer> orderCountList;
    
    /**
     * 订单金额列表
     */
    private List<BigDecimal> orderAmountList;
} 