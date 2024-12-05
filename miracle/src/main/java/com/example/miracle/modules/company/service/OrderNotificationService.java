package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.OrderNotification;

public interface OrderNotificationService extends IService<OrderNotification> {
    
    /**
     * 创建通知
     */
    void createNotification(Long orderId, String orderNo, Integer type, String content);
    
    /**
     * 发送通知
     */
    void sendNotification(Long id);
    
    /**
     * 处理失败通知
     */
    void handleFailedNotification(Long id, String reason);
} 