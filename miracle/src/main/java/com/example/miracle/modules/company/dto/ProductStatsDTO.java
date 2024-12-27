package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class ProductStatsDTO {

    private Long totalViews;        // 总浏览量
    private Long totalIntentions;   // 总意向数
    private Long todayViews;        // 今日浏览
    private Long todayIntentions;   // 今日意向
}
