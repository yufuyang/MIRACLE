package com.example.miracle.modules.company.service.impl;

import com.example.miracle.common.constant.PayStatusEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.service.PayHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class CashPayHandler implements PayHandler {
    @Override
    public PayResultDTO handlePay(Order order, OrderPayment payment, OrderPayDTO dto) {
        if (dto.getPayAmount() == null) {
            throw new BusinessException("请填写支付金额");
        }

        // 校验支付金额
        if (order.getPayAmount().compareTo(dto.getPayAmount()) != 0) {
            throw new BusinessException("支付金额不正确");
        }

        // 生成流水号
        String tradeNo = "CASH_" + System.currentTimeMillis();
        LocalDateTime payTime = LocalDateTime.now();

        // 更新支付记录
        payment.setTradeNo(tradeNo);
        payment.setPayTime(payTime);
        payment.setPayStatus(PayStatusEnum.PAID.getCode());

        // 返回支付结果
        PayResultDTO result = new PayResultDTO();
        result.setOrderNo(order.getOrderNo());
        result.setPayType(dto.getPayType());
        result.setPayAmount(order.getPayAmount());
        result.setPayStatus(PayStatusEnum.PAID.getCode());
        result.setPayTime(payTime);
        result.setTradeNo(tradeNo);
        return result;
    }
} 