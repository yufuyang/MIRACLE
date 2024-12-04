package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.OrderItem;
import com.example.miracle.modules.company.mapper.OrderItemMapper;
import com.example.miracle.modules.company.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return this.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderItems(Long orderId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }

        // 设置订单ID
        orderItems.forEach(item -> {
            item.setOrderId(orderId);
            // 计算商品总金额
            item.setTotalAmount(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        });

        this.saveBatch(orderItems);
    }

    @Override
    public BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderItems.stream()
                .map(OrderItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByOrderId(Long orderId) {
        this.lambdaUpdate()
                .eq(OrderItem::getOrderId, orderId)
                .remove();
    }
}