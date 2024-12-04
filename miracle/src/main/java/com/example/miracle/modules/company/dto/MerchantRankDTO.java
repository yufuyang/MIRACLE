package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantRankDTO {

    private Long merchantId;
    private String merchantName;
    private BigDecimal salesAmount;
    private Integer salesCount;
}
