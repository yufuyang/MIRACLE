package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@TableName("t_order_delivery")
@EqualsAndHashCode(callSuper = true)
public class OrderDelivery extends BaseEntity {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 物流公司
     */
    private String expressCompany;
    
    /**
     * 物流单号
     */
    private String expressNo;
    
    /**
     * 收货人
     */
    private String receiverName;
    
    /**
     * 收货电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;
    
    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 物流状态：0-待发货 1-已发货 2-已签收
     */
    private Integer status;
    
    /**
     * 物流跟踪信息
     */
    private String trackingInfo;
} 