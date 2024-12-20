package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityRegistrationDTO {
    
    /**
     * ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 状态:0-待审核,1-已通过,2-已拒绝
     */
    private Integer status;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 报名时间
     */
    private LocalDateTime createTime;
} 