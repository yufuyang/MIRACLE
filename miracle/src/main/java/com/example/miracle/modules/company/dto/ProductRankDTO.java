package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class ProductRankDTO {

    private Long id;               // 产品ID
    private String productName;    // 产品名称
    private Long views;           // 浏览量
    private Long intentions;      // 意向数
    private Long lastViews;        // 总浏览量
    private Long lastIntentions;   // 总意向数
}
