package com.example.miracle.modules.company.service;

import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import java.math.BigDecimal;

public interface PayService {

    PayResultDTO pay(OrderPayDTO dto);


     void closePayment(String orderNo);

    /**
     * 退款
     */
    void refund(String orderNo, BigDecimal amount);
}
