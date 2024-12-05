package com.example.miracle.modules.company.service.impl;

import com.example.miracle.common.constant.RefundTypeEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.RefundOrder;
import com.example.miracle.modules.company.service.OrderService;
import com.example.miracle.modules.company.service.PayService;
import com.example.miracle.modules.company.service.RefundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnlyRefundHandler implements RefundHandler {
    
    private final OrderService orderService;
    private final PayService payService;
    
    @Override
    public void handleRefund(RefundOrder refundOrder) {
        // 调用支付平台退款接口
        payService.refund(refundOrder.getOrderNo(), refundOrder.getRefundAmount());
    }
    
    @Override
    public void validateRefund(RefundOrder refundOrder) {
        Order order = orderService.getById(refundOrder.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (refundOrder.getRefundAmount().compareTo(order.getPayAmount()) > 0) {
            throw new BusinessException("退款金额不能大于支付金额");
        }
    }
    
    @Override
    public Integer getRefundType() {
        return RefundTypeEnum.ONLY_REFUND.getCode();
    }
} 