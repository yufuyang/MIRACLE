package com.example.miracle.modules.company.service;

import com.example.miracle.modules.company.entity.RefundOrder;

public interface RefundHandler {
    /**
     * 处理退款
     */
    void handleRefund(RefundOrder refundOrder);

    /**
     * 验证退款
     */
    void validateRefund(RefundOrder refundOrder);

    /**
     * 获取处理器类型
     */
    Integer getRefundType();
} 