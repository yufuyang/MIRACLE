package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("activity_registration")
@EqualsAndHashCode(callSuper = true)
public class ActivityRegistration extends BaseEntity {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 用户ID
     */
    private Long merchantId;


    /**
     * 状态:0-待审核,1-已通过,2-已拒绝
     */
    private Integer status;
} 