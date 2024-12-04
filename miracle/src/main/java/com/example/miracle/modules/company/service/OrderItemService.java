package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单ID获取订单商品列表
     */
    List<OrderItem> getOrderItems(Long orderId);

    /**
     * 批量保存订单商品
     */
    void saveOrderItems(Long orderId, List<OrderItem> orderItems);

    /**
     * 计算订单商品总金额
     */
    BigDecimal calculateTotalAmount(List<OrderItem> orderItems);

    /**
     * 删除订单商品
     */
    void deleteByOrderId(Long orderId);
}