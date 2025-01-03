package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class ActivityStatsDTO {
    private Integer totalCount;     // 总活动数
    private Integer activeCount;    // 进行中活动数
    private Integer viewCount;      // 总浏览量
    private Integer registerCount;  // 总报名数
} 