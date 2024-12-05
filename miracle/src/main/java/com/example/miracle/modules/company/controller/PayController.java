package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.service.OrderPaymentService;
import com.example.miracle.modules.company.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    private final OrderPaymentService orderPaymentService;


    /**
     * 订单支付
     */
    @PostMapping
    public ResultDTO<PayResultDTO> pay(@RequestBody OrderPayDTO dto) {

        return ResultDTO.ok(payService.pay(dto));
    }

    /**
     * 查询支付记录
     */
    @GetMapping("/record")
    public ResultDTO<OrderPayment> getPaymentRecord(@RequestParam String orderNo) {
        OrderPayment payment = orderPaymentService.lambdaQuery()
                .eq(OrderPayment::getOrderNo, orderNo)
                .orderByDesc(OrderPayment::getCreateTime)
                .last("LIMIT 1")
                .one();
        return ResultDTO.ok(payment);
    }

    /**
     * 关闭支付
     */
    @PostMapping("/close")
    public ResultDTO<?> closePayment(@RequestParam String orderNo) {
        payService.closePayment(orderNo);
        return ResultDTO.ok();
    }
}