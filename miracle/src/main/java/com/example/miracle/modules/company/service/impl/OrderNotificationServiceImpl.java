package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.OrderNotification;
import com.example.miracle.modules.company.mapper.OrderNotificationMapper;
import com.example.miracle.modules.company.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderNotificationServiceImpl extends ServiceImpl<OrderNotificationMapper, OrderNotification> implements OrderNotificationService {
    
    @Override
    public void createNotification(Long orderId, String orderNo, Integer type, String content) {
        OrderNotification notification = new OrderNotification();
        notification.setOrderId(orderId);
        notification.setOrderNo(orderNo);
        notification.setType(type);
        notification.setContent(content);
        notification.setStatus(0);
        this.save(notification);
    }
    
    @Override
    public void sendNotification(Long id) {
        // TODO: 实现具体的通知发送逻辑
        this.lambdaUpdate()
                .set(OrderNotification::getStatus, 1)
                .eq(OrderNotification::getId, id)
                .update();
    }
    
    @Override
    public void handleFailedNotification(Long id, String reason) {
        this.lambdaUpdate()
                .set(OrderNotification::getStatus, 2)
                .set(OrderNotification::getFailReason, reason)
                .set(OrderNotification::getRetryCount, 
                    this.getById(id).getRetryCount() + 1)
                .set(OrderNotification::getNextRetryTime, 
                    LocalDateTime.now().plusMinutes(5))
                .eq(OrderNotification::getId, id)
                .update();
    }
} 