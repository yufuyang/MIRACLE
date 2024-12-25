package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("activity")
@EqualsAndHashCode(callSuper = true)
public class Activity extends BaseEntity {

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
} 