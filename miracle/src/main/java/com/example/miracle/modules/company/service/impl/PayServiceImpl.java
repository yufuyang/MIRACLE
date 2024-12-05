package com.example.miracle.modules.company.service.impl;

import com.example.miracle.common.constant.OrderStatusEnum;
import com.example.miracle.common.constant.PayStatusEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.OrderPayDTO;
import com.example.miracle.modules.company.dto.PayResultDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.OrderPayment;
import com.example.miracle.modules.company.entity.PayMethodConfig;
import com.example.miracle.modules.company.factory.PayHandlerFactory;
import com.example.miracle.modules.company.factory.RefundPayHandlerFactory;
import com.example.miracle.modules.company.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayServiceImpl implements PayService {

    private final OrderService orderService;
    private final PayMethodConfigService payMethodConfigService;
    private final OrderPaymentService orderPaymentService;

    private final OrderLogService orderLogService;
    private final PayHandlerFactory payHandlerFactory;
    private final StringRedisTemplate redisTemplate;
    private final RefundPayHandlerFactory refundPayHandlerFactory;

    private boolean tryLock(String key, long seconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", seconds, TimeUnit.SECONDS));
    }

    private void unlock(String key) {
        redisTemplate.delete(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDTO pay(OrderPayDTO dto) {
        String lockKey = "order_pay:" + dto.getOrderNo();
        
        if (!tryLock(lockKey, 30)) {
            throw new BusinessException("订单正在处理中，请稍后重试");
        }
        
        try {
            // 1. 查询订单
            Order order = orderService.lambdaQuery()
                    .eq(Order::getOrderNo, dto.getOrderNo())
                    .one();

            if (order == null) {
                throw new BusinessException("订单不存在");
            }

            // 2. 校验订单状态
            if (!OrderStatusEnum.UNPAID.equals(order.getStatus())) {
                throw new BusinessException("订单状态不正确");
            }

            // 校验支付方式
            if (!dto.getPayType().equals(order.getPayType())) {
                throw new BusinessException("支付方式与订单不符");
            }

            // 校验支付配置
            PayMethodConfig payConfig = payMethodConfigService.getById(order.getPayMethodId());
            if (payConfig == null || !payConfig.getStatus().equals(1)) {
                throw new BusinessException("支付方式未配置或已禁用");
            }

            // 4. 创建支付记录
            OrderPayment payment = createPayment(order, dto);
            payment.setConfigId(payConfig.getId());  // 关联支付配置ID
            payment.setConfig(payConfig.getConfig()); // 保存支付配置信息
            orderPaymentService.updateById(payment);

            try {
                // 5. 调用支付处理器
                PayResultDTO result = payHandlerFactory.getHandler(dto.getPayType())
                        .orElseThrow(() -> new BusinessException("不支持的支付方式"))
                        .handlePay(order, payment, dto);

                // 6. 处理支付结果
                if (PayStatusEnum.PAID.getCode().equals(result.getPayStatus())) {
                    completePayment(order, payment, dto);
                } else if (PayStatusEnum.FAILED.getCode().equals(result.getPayStatus())) {
                    handlePaymentFailed(payment, result.getFailReason());
                }
                
                return result;
            } catch (Exception e) {
                handlePaymentFailed(payment, e.getMessage());
                throw e;
            }
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 获取支付处理器
     */
    private PayHandler getPayHandler(Integer payType) {
        return payHandlerFactory.getHandler(payType)
                .orElseThrow(() -> new BusinessException("不支持的支付方式"));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(String orderNo, BigDecimal amount) {
        String lockKey = "refund:" + orderNo;
        if (!tryLock(lockKey, 30)) {
            throw new BusinessException("退款处理中，请稍后重试");
        }

        try {
            // 1. 查询订单支付记录
            OrderPayment payment = orderPaymentService.lambdaQuery()
                    .eq(OrderPayment::getOrderNo, orderNo)
                    .eq(OrderPayment::getPayStatus, PayStatusEnum.PAID.getCode())
                    .one();

            if (payment == null) {
                throw new BusinessException("未找到支付记录");
            }

            // 2. 校验退款金额
            if (payment.getRefundAmount().add(amount).compareTo(payment.getPayAmount()) > 0) {
                throw new BusinessException("退款金额超过支付金额");
            }

            try {
                // 3. 调用对应的退款处理器
                RefundPayHandler handler = refundPayHandlerFactory.getHandler(payment.getPayType())
                        .orElseThrow(() -> new BusinessException("不支持的支付方式"));
                handler.handleRefund(payment, amount);

                // 4. 更新支付记录状态
                payment.setRefundAmount(payment.getRefundAmount().add(amount));
                payment.setRefundTime(LocalDateTime.now());
                payment.setPayStatus(PayStatusEnum.REFUNDED.getCode());
                orderPaymentService.updateById(payment);

                // 5. 记录退款日志
                orderLogService.recordLog(payment.getOrderId(), "SYSTEM", "退款完成",
                        String.format("退款金额：%s", amount));

            } catch (Exception e) {
                // 6. 记录退款失败日志
                orderLogService.recordLog(payment.getOrderId(), "SYSTEM", "退款失败", e.getMessage());
                throw e;
            }
        } finally {
            unlock(lockKey);
        }
    }

}
