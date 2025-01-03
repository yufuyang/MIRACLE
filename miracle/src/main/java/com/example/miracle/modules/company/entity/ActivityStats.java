package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@TableName("activity_stats")
@EqualsAndHashCode(callSuper = true)
public class ActivityStats extends BaseEntity {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 报名人数
     */
    private Integer registerCount;

    private LocalDate statsDate;
} 