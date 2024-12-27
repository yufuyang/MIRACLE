package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductTrendDTO {

    private List<String> dates;     // 日期列表
    private List<Long> views;       // 浏览量数据
    private List<Long> intentions;  // 意向数数据
}
