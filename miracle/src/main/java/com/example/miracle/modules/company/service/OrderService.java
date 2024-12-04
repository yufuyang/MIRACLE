package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.OrderCreateDTO;
import com.example.miracle.modules.company.dto.OrderPayCallbackDTO;
import com.example.miracle.modules.company.entity.Order;

import java.time.LocalDateTime;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Order createOrder(OrderCreateDTO dto, String operator);

    /**
     * 分页查询订单
     */
    Page<Order> pageOrder(Integer current, Integer size, Long merchantId,
                          String orderNo, Integer status,
                          LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取订单详情
     */
    Order getOrderDetail(Long id);

    /**
     * 取消订单
     */
    void cancelOrder(Long id, String cancelReason, String operator);

    /**
     * 发货
     */
    void deliver(Long id, String operator);

    /**
     * 完成订单
     */
    void complete(Long id, String operator);

    /**
     * 退款
     */
    void refund(Long id, String operator);


    /**
     * 处理支付回调
     */
    void handlePayCallback(OrderPayCallbackDTO dto);

    /**
     * 处理订单超时
     */
    void handleOrderTimeout();
}
