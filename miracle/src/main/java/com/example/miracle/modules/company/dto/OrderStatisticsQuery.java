package com.example.miracle.modules.company.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderStatisticsQuery {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 统计类型：1-按天 2-按月 3-按年
     */
    private Integer type;
} 