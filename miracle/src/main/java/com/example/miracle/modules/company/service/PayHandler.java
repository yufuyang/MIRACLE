package com.example.miracle.modules.company.service;

import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.OrderPayment;

public interface PayHandler {
    PayResultDTO handlePay(Order order, OrderPayment payment, OrderPayDTO dto);
} 