package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@TableName("t_order_notification")
@EqualsAndHashCode(callSuper = true)
public class OrderNotification extends BaseEntity {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 通知类型：1-支付 2-发货 3-退款 4-完成
     */
    private Integer type;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知状态：0-待发送 1-已发送 2-发送失败
     */
    private Integer status;
    
    /**
     * 失败原因
     */
    private String failReason;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;
} 