package com.example.miracle.modules.company.service.impl;

import com.example.miracle.common.constant.PayStatusEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.entity.PayMethodConfig;
import com.example.miracle.modules.company.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayServiceImpl implements PayService {

    private final OrderService orderService;
    private final PayMethodConfigService payMethodConfigService;
    private final OrderPaymentService orderPaymentService;

    private final OrderLogService orderLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDTO pay(OrderPayDTO dto) {
        // 1. 查询订单
        Order order = orderService.lambdaQuery()
                .eq(Order::getOrderNo, dto.getOrderNo())
                .one();

        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        // 3. 校验支付方式是否可用
        PayMethodConfig payMethod = payMethodConfigService.lambdaQuery()
                .eq(PayMethodConfig::getMerchantId, order.getMerchantId())
                .eq(PayMethodConfig::getPayType, dto.getPayType())
                .eq(PayMethodConfig::getStatus, 1)
                .one();

        if (payMethod == null) {
            throw new BusinessException("支付方式不可用");
        }

        // 4. 创建支付记录
        OrderPayment payment = createPayment(order, dto);

        try {
            // 5. 根据支付方式处理支付
            switch (dto.getPayType()) {
                case 1: // 微信支付
                    return handleWxPay(order, payment, dto);
                case 2: // 支付宝支付
                    return handleAliPay(order, payment, dto);
                case 3: // 现金支付
                    return handleCashPay(order, payment, dto);
                default:
                    throw new BusinessException("不支持的支付方式");
            }
        } catch (Exception e) {
            // 6. 处理支付失败
            handlePaymentFailed(payment, e.getMessage());

            PayResultDTO result = new PayResultDTO();
            result.setOrderNo(order.getOrderNo());
            result.setPayType(dto.getPayType());
            result.setPayAmount(order.getPayAmount());
            result.setPayStatus(PayStatusEnum.FAILED.getCode());
            result.setFailReason(e.getMessage());
            return result;
        }
    }

    /**
     * 创建支付记录
     */
    private OrderPayment createPayment(Order order, OrderPayDTO dto) {
        OrderPayment payment = new OrderPayment();
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setPayType(dto.getPayType());
        payment.setPayAmount(order.getPayAmount());
        payment.setPayStatus(PayStatusEnum.PAYING.getCode());
        orderPaymentService.save(payment);
        return payment;
    }

    /**
     * 处理现金支付
     */
    private PayResultDTO handleCashPay(Order order, OrderPayment payment, OrderPayDTO dto) {
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
        orderPaymentService.updateById(payment);

        // 完成支付
        completePayment(order, payment, dto);

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

    private PayResultDTO handleWxPay(Order order, OrderPayment payment, OrderPayDTO dto) {
        // 1. 调用微信支付接口
        // 2. 处理支付结果
        PayResultDTO result = new PayResultDTO();
        result.setOrderNo(order.getOrderNo());
        result.setPayType(dto.getPayType());
        result.setPayAmount(order.getPayAmount());
        result.setPayStatus(PayStatusEnum.PAYING.getCode());
        // 这里添加微信支付参数
        result.setPayParams(new HashMap<>());
        return result;
    }


    private PayResultDTO handleAliPay(Order order, OrderPayment payment, OrderPayDTO dto) {
        // 1. 调用支付宝接口
        // 2. 处理支付结果

        PayResultDTO result = new PayResultDTO();
        result.setOrderNo(order.getOrderNo());
        result.setPayType(dto.getPayType());
        result.setPayAmount(order.getPayAmount());
        result.setPayStatus(PayStatusEnum.PAYING.getCode());
        // 这里添加微信支付参数
        result.setPayParams(new HashMap<>());
        return result;
    }

    /**
     * 完成支付
     */
    private void completePayment(Order order, OrderPayment payment, OrderPayDTO dto) {
        // 更新订单状态
        order.setStatus(1);
        order.setPayType(dto.getPayType());
        order.setPayTime(payment.getPayTime());
        orderService.updateById(order);

        // 记录订单日志
        orderLogService.recordLog(order.getId(), dto.getOperator(), "支付完成",
                String.format("支付方式：%s，支付金额：%s，流水号：%s",
                        dto.getPayType(), payment.getPayAmount(), payment.getTradeNo()));
    }

    /**
     * 处理支付失败
     */
    private void handlePaymentFailed(OrderPayment payment, String reason) {
        payment.setPayStatus(PayStatusEnum.FAILED.getCode());
        payment.setFailReason(reason);
        orderPaymentService.updateById(payment);
    }

    /**
     * 关闭支付
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePayment(String orderNo) {
        // 查询未完成的支付记录
        OrderPayment payment = orderPaymentService.lambdaQuery()
                .eq(OrderPayment::getOrderNo, orderNo)
                .in(OrderPayment::getPayStatus, PayStatusEnum.UNPAID.getCode(), PayStatusEnum.PAYING.getCode())
                .one();

        if (payment != null) {
            payment.setPayStatus(PayStatusEnum.CLOSED.getCode());
            orderPaymentService.updateById(payment);
        }
    }

}
