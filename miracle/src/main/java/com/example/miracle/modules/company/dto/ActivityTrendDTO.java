package com.example.miracle.modules.company.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActivityTrendDTO {
    private List<String> dates;        // 日期列表
    private List<Integer> viewCounts;  // 浏览量列表
    private List<Integer> regCounts;   // 报名数列表
} 