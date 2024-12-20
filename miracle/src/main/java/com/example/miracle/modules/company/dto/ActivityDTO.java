package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    
    /**
     * ID
     */
    private Long id;

    /**
     * 所属企业ID
     */
    private Long companyId;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;


    /**
     * 状态:0-未开始,1-进行中,2-已结束
     */
    private Integer status;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 报名人数
     */
    private Integer registerCount;
} 