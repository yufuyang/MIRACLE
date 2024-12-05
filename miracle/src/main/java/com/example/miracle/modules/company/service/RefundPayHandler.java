package com.example.miracle.modules.company.service;

import com.example.miracle.modules.company.entity.OrderPayment;
import java.math.BigDecimal;

public interface RefundPayHandler {
    /**
     * 处理退款
     */
    void handleRefund(OrderPayment payment, BigDecimal amount);

    /**
     * 获取支付类型
     */
    Integer getPayType();
} 