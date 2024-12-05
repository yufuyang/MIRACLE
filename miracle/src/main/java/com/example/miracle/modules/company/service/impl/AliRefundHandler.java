package com.example.miracle.modules.company.service.impl;

import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.service.RefundPayHandler;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AliRefundHandler implements RefundPayHandler {
    
    @Override
    public void handleRefund(OrderPayment payment, BigDecimal amount) {
        // TODO: 调用支付宝退款接口
        log.info("调用支付宝退款接口，订单号：{}，退款金额：{}", payment.getOrderNo(), amount);
    }
    
    @Override
    public Integer getPayType() {
        return 2; // 支付宝支付
    }
} 