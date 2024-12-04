package com.example.miracle.modules.company.service;

import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;

public interface PayService {

    PayResultDTO pay(OrderPayDTO dto);


     void closePayment(String orderNo);
}
