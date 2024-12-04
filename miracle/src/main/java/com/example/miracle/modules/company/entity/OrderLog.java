package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("order_log")
@EqualsAndHashCode(callSuper = true)
public class OrderLog extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作行为
     */
    private String action;

    /**
     * 操作内容
     */
    private String content;
}