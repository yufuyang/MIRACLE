package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HourlyStatDTO {

    private Integer hour;
    private Integer orderCount;
    private BigDecimal salesAmount;
}
