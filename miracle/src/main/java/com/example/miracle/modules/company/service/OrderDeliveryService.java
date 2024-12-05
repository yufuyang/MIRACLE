package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.OrderDelivery;

public interface OrderDeliveryService extends IService<OrderDelivery> {
    
    /**
     * 获取订单发货信息
     */
    OrderDelivery getByOrderNo(String orderNo);
    
    /**
     * 更新物流状态
     */
    void updateDeliveryStatus(Long id, Integer status);
} 