package com.example.miracle.modules.company.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
} 