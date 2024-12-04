package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductRankDTO {

    private Long productId;
    private String productName;
    private Integer salesCount;
    private BigDecimal salesAmount;
}
