package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.mapper.OrderPaymentMapper;
import com.example.miracle.modules.company.service.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderPaymentServiceImpl extends ServiceImpl<OrderPaymentMapper, OrderPayment> implements OrderPaymentService {

    @Override
    public OrderPayment getByOrderId(Long orderId) {
        return this.lambdaQuery()
                .eq(OrderPayment::getOrderId, orderId)
                .one();
    }

    @Override
    public OrderPayment getByTradeNo(String tradeNo) {
        return this.lambdaQuery()
                .eq(OrderPayment::getTradeNo, tradeNo)
                .one();
    }

    @Override
    public boolean isOrderPaid(Long orderId) {
        return this.lambdaQuery()
                .eq(OrderPayment::getOrderId, orderId)
                .exists();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPayment(OrderPayment payment) {
        // 检查支付流水号是否重复
        if (this.getByTradeNo(payment.getTradeNo()) != null) {
            throw new BusinessException("支付流水号重复");
        }

        // 检查订单是否已支付
        if (this.isOrderPaid(payment.getOrderId())) {
            throw new BusinessException("订单已支付");
        }

        this.save(payment);
    }
}