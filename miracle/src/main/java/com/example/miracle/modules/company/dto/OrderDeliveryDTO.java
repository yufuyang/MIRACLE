package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class OrderDeliveryDTO {
    
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
     * 操作人
     */
    private String operator;
} 