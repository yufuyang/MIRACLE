package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.OrderPayment;

public interface OrderPaymentService extends IService<OrderPayment> {

    /**
     * 根据订单ID获取支付记录
     */
    OrderPayment getByOrderId(Long orderId);

    /**
     * 根据支付流水号查询支付记录
     */
    OrderPayment getByTradeNo(String tradeNo);

    /**
     * 检查订单是否已支付
     */
    boolean isOrderPaid(Long orderId);

    /**
     * 创建支付记录
     */
    void createPayment(OrderPayment payment);
}