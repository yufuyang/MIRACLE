package com.example.miracle.modules.company.service.impl;

import com.example.miracle.common.constant.PayStatusEnum;
import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.service.PayHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AliPayHandler implements PayHandler {
    @Override
    public PayResultDTO handlePay(Order order, OrderPayment payment, OrderPayDTO dto) {
        // TODO: 调用支付宝支付接口
        PayResultDTO result = new PayResultDTO();
        result.setOrderNo(order.getOrderNo());
        result.setPayType(dto.getPayType());
        result.setPayAmount(order.getPayAmount());
        result.setPayStatus(PayStatusEnum.PAYING.getCode());
        result.setPayParams(new HashMap<>());  // 这里设置支付宝支付需要的参数
        return result;
    }
} 