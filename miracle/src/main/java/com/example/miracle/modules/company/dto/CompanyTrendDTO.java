package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class CompanyTrendDTO {

    /**
     * 日期列表
     */
    private List<LocalDate> dateList;

    /**
     * 商户数量趋势
     */
    private List<Integer> merchantCountList;

    /**
     * 销售额趋势
     */
    private List<BigDecimal> salesAmountList;

    /**
     * 订单数趋势
     */
    private List<Integer> orderCountList;
}
