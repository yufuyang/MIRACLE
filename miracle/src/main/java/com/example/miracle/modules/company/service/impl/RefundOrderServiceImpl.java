package com.example.miracle.modules.company.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.OrderStatusEnum;
import com.example.miracle.common.constant.PayStatusEnum;
import com.example.miracle.common.constant.RefundStatusEnum;
import com.example.miracle.common.constant.RefundTypeEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.*;
import com.example.miracle.modules.company.entity.*;
import com.example.miracle.modules.company.factory.RefundHandlerFactory;
import com.example.miracle.modules.company.factory.RefundPayHandlerFactory;
import com.example.miracle.modules.company.mapper.RefundOrderMapper;
import com.example.miracle.modules.company.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundOrderServiceImpl extends ServiceImpl<RefundOrderMapper, RefundOrder> implements RefundOrderService {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final ReturnOrderService returnOrderService;
    private final ReturnItemService returnItemService;
    private final ProductService productService;
    private final OrderLogService orderLogService;
    private final StringRedisTemplate redisTemplate;
    private final RefundHandlerFactory refundHandlerFactory;
    private final OrderPaymentService orderPaymentService;
    private final RefundPayHandlerFactory refundPayHandlerFactory;

    private static final String REFUND_LOCK_KEY = "refund:lock:";
    private static final int MAX_RETRY_TIMES = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundOrder applyRefund(RefundApplyDTO dto) {
        // 1. 查询订单
        Order order = orderService.lambdaQuery()
                .eq(Order::getOrderNo, dto.getOrderNo())
                .one();

        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (order.getStatus() != OrderStatusEnum.PAID.getCode()) {
            throw new BusinessException("订单状态不正确，只有已支付的订单才能申请退款");
        }

        // 3. 校验退款金额
        if (dto.getRefundAmount().compareTo(order.getPayAmount()) > 0) {
            throw new BusinessException("退款金额不能大于支付金额");
        }

        // 4. 校验是否存在进行中的退款
        boolean existsProcessing = this.lambdaQuery()
                .eq(RefundOrder::getOrderId, order.getId())
                .in(RefundOrder::getStatus,
                        RefundStatusEnum.PENDING.getCode(),
                        RefundStatusEnum.APPROVED.getCode(),
                        RefundStatusEnum.PROCESSING.getCode())
                .exists();

        if (existsProcessing) {
            throw new BusinessException("该订单已存在进行中的退款申请");
        }

        // 5. 校验退款总金额
        BigDecimal totalRefundAmount = this.lambdaQuery()
                .eq(RefundOrder::getOrderId, order.getId())
                .eq(RefundOrder::getStatus, RefundStatusEnum.COMPLETED.getCode())
                .select(RefundOrder::getRefundAmount)
                .list()
                .stream()
                .map(RefundOrder::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRefundAmount.add(dto.getRefundAmount()).compareTo(order.getPayAmount()) > 0) {
            throw new BusinessException("退款总金额不能超过支付金额");
        }

        // 6. 如果是退货退款，校验退货商品
        if (RefundTypeEnum.RETURN_REFUND.getCode().equals(dto.getRefundType())) {
            validateRefundItems(order.getId(), dto.getItems());
        }

        // 7. 创建退款单
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundNo(generateRefundNo());
        refundOrder.setOrderId(order.getId());
        refundOrder.setOrderNo(order.getOrderNo());
        refundOrder.setMerchantId(order.getMerchantId());
        refundOrder.setRefundType(dto.getRefundType());
        refundOrder.setRefundAmount(dto.getRefundAmount());
        refundOrder.setRefundReason(dto.getRefundReason());
        refundOrder.setStatus(RefundStatusEnum.PENDING.getCode());
        refundOrder.setOperator(dto.getOperator());
        this.save(refundOrder);

        // 8. 如果是退货退款，创建退货单
        if (RefundTypeEnum.RETURN_REFUND.getCode().equals(dto.getRefundType())) {
            returnOrderService.createReturnOrder(refundOrder, dto.getItems());
        }

        // 9. 记录订单日志
        orderLogService.recordLog(order.getId(), dto.getOperator(), "申请退款",
                String.format("退款单号：%s，退款金额：%s，退款原因：%s",
                        refundOrder.getRefundNo(), dto.getRefundAmount(), dto.getRefundReason()));

        return refundOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditRefund(RefundAuditDTO dto) {
        // 1. 查询退款单
        RefundOrder refundOrder = this.lambdaQuery()
                .eq(RefundOrder::getRefundNo, dto.getRefundNo())
                .one();

        if (refundOrder == null) {
            throw new BusinessException("退款单不存在");
        }

        // 2. 校验退款单状态
        if (!RefundStatusEnum.PENDING.getCode().equals(refundOrder.getStatus())) {
            throw new BusinessException("退款单状态不正确");
        }

        // 3. 更新退款单状态
        RefundOrder updateRefund = new RefundOrder();
        updateRefund.setId(refundOrder.getId());
        updateRefund.setStatus(dto.getAuditStatus());
        updateRefund.setAuditUser(dto.getOperator());
        updateRefund.setAuditTime(LocalDateTime.now());
        updateRefund.setAuditRemark(dto.getAuditRemark());
        this.updateById(updateRefund);

        // 4. 如果是退货退款且审核通过，更新退货单信息
        if (RefundTypeEnum.RETURN_REFUND.getCode().equals(refundOrder.getRefundType())
                && RefundStatusEnum.APPROVED.getCode().equals(dto.getAuditStatus())) {
            returnOrderService.updateReturnAddress(refundOrder.getRefundNo(), dto.getReturnAddress());
        }

        // 5. 如果是仅退款且审核通过，直接处理退款
        if (RefundTypeEnum.ONLY_REFUND.getCode().equals(refundOrder.getRefundType())
                && RefundStatusEnum.APPROVED.getCode().equals(dto.getAuditStatus())) {
            this.processRefund(refundOrder.getRefundNo());
        }

        // 6. 记录订单日志
        String logContent = dto.getAuditStatus().equals(RefundStatusEnum.APPROVED.getCode()) ? "审核通过" : "审核拒绝";
        orderLogService.recordLog(refundOrder.getOrderId(), dto.getOperator(), "退款审核-" + logContent, dto.getAuditRemark());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRefund(String refundNo) {
        String lockKey = REFUND_LOCK_KEY + refundNo;
        if (!tryLock(lockKey, 30)) {
            throw new BusinessException("退款处理中，请稍后重试");
        }

        try {
            // 1. 查询退款单
            RefundOrder refundOrder = getByRefundNo(refundNo);
            if (refundOrder == null) {
                throw new BusinessException("退款单不存在");
            }

            // 2. 查询原支付记录
            OrderPayment payment = orderPaymentService.lambdaQuery()
                    .eq(OrderPayment::getOrderNo, refundOrder.getOrderNo())
                    .eq(OrderPayment::getPayStatus, PayStatusEnum.PAID.getCode())
                    .one();

            if (payment == null) {
                throw new BusinessException("未找到支付记录");
            }

            try {
                // 3. 根据原支付方式进行退款
                RefundPayHandler handler = refundPayHandlerFactory.getHandler(payment.getPayType())
                        .orElseThrow(() -> new BusinessException("不支持的退款方式"));
                handler.handleRefund(payment, refundOrder.getRefundAmount());

                // 4. 更新退款单状态
                refundOrder.setStatus(RefundStatusEnum.COMPLETED.getCode());
                refundOrder.setCompleteTime(LocalDateTime.now());
                this.updateById(refundOrder);

                // 5. 更新订单状态
                orderService.updateOrderStatus(refundOrder.getOrderId(), 
                    OrderStatusEnum.REFUNDED.getCode(), "SYSTEM", "退款完成");

                // 6. 记录退款日志
                orderLogService.recordLog(refundOrder.getOrderId(), "SYSTEM", "退款完成",
                    String.format("退款金额：%s，原支付方式：%s", 
                        refundOrder.getRefundAmount(), payment.getPayMethodName()));

            } catch (Exception e) {
                // 退款失败处理
                handleRefundFailure(refundOrder, e);
            }
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 完成退款
     */
    private void completeRefund(RefundOrder refundOrder) {
        this.lambdaUpdate()
                .set(RefundOrder::getStatus, RefundStatusEnum.COMPLETED.getCode())
                .set(RefundOrder::getCompleteTime, LocalDateTime.now())
                .eq(RefundOrder::getId, refundOrder.getId())
                .update();

        // 记录订单日志
        orderLogService.recordLog(refundOrder.getOrderId(), "SYSTEM", "退款完成",
                String.format("退款金额：%s，退款单号：%s", refundOrder.getRefundAmount(), refundOrder.getRefundNo()));
    }

    /**
     * 拒绝退款
     */
    private void rejectRefund(RefundOrder refundOrder, String reason) {
        this.lambdaUpdate()
                .set(RefundOrder::getStatus, RefundStatusEnum.REJECTED.getCode())
                .set(RefundOrder::getRejectReason, reason)
                .eq(RefundOrder::getId, refundOrder.getId())
                .update();

        // 记录订单日志
        orderLogService.recordLog(refundOrder.getOrderId(), "SYSTEM", "退款失败", reason);
    }

    /**
     * 发送退款通知
     */
    private void sendRefundNotification(RefundOrder refundOrder, String message) {
        // TODO: 实现退款通知逻辑，如发送短信、邮件等
        log.info("发送退款通知，退款单号：{}，消息：{}", refundOrder.getRefundNo(), message);
    }

    private boolean tryLock(String key, long seconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", seconds, TimeUnit.SECONDS));
    }

    private void unlock(String key) {
        redisTemplate.delete(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRefund(String refundNo, String operator) {
        // 1. 查询退款单
        RefundOrder refundOrder = this.lambdaQuery()
                .eq(RefundOrder::getRefundNo, refundNo)
                .one();

        if (refundOrder == null) {
            throw new BusinessException("退款单不存在");
        }

        // 2. 校验退款单状态
        if (!RefundStatusEnum.PENDING.getCode().equals(refundOrder.getStatus())) {
            throw new BusinessException("只能取消待审核的退款单");
        }

        // 3. 更新退款单状态
        this.lambdaUpdate()
                .set(RefundOrder::getStatus, RefundStatusEnum.CANCELLED.getCode())
                .eq(RefundOrder::getId, refundOrder.getId())
                .update();

        // 4. 如果是退货退款，同时取消退货单
        if (RefundTypeEnum.RETURN_REFUND.getCode().equals(refundOrder.getRefundType())) {
            returnOrderService.cancelReturn(refundNo);
        }

        // 5. 记录订单日志
        orderLogService.recordLog(refundOrder.getOrderId(), operator, "取消退款", null);
    }

    @Override
    public RefundDetailDTO getRefundDetail(String refundNo) {
        // 1. 查询退款单
        RefundOrder refundOrder = this.lambdaQuery()
                .eq(RefundOrder::getRefundNo, refundNo)
                .one();

        if (refundOrder == null) {
            throw new BusinessException("退款单不存在");
        }

        // 2. 构建详情VO
        RefundDetailDTO vo = new RefundDetailDTO();
        BeanUtils.copyProperties(refundOrder, vo);

        // 3. 如果是退货退款，查询退货信息
        if (RefundTypeEnum.RETURN_REFUND.getCode().equals(refundOrder.getRefundType())) {
            ReturnOrder returnOrder = returnOrderService.lambdaQuery().eq(ReturnOrder::getRefundNo, refundNo).one();
            if (returnOrder != null) {
                ReturnDetailDTO returnDetail = new ReturnDetailDTO();
                BeanUtils.copyProperties(returnOrder, returnDetail);
                // 查询退货商品
                List<ReturnItem> items = returnItemService.getReturnItems(returnOrder.getId());
                returnDetail.setItems(items);
                vo.setReturnDetail(returnDetail);
            }
        }

        return vo;
    }

    /**
     * 校验退货商品
     */
    private void validateRefundItems(Long orderId, List<RefundItemDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            throw new BusinessException("请选择退货商品");
        }

        // 1. 查询订单商品
        List<OrderItem> orderItems = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();

        // 2. 校验退货数量
        for (RefundItemDTO item : items) {
            OrderItem orderItem = orderItems.stream()
                    .filter(oi -> oi.getId().equals(item.getOrderItemId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("订单商品不存在"));

            // 查询已退货数量
            int refundedQuantity = returnOrderService.getRefundedQuantity(orderItem.getId());

            // 校验退货数量
            if (item.getQuantity() > orderItem.getQuantity() - refundedQuantity) {
                throw new BusinessException(String.format("商品[%s]退货数量超过可退数量",
                        orderItem.getProductName()));
            }
        }
    }

    /**
     * 生成退款单号
     */
    private String generateRefundNo() {
        return "RF" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + RandomUtil.randomNumbers(6);
    }

    /**
     * 根据退款单号查询退款单
     */
    private RefundOrder getByRefundNo(String refundNo) {
        return this.lambdaQuery()
                .eq(RefundOrder::getRefundNo, refundNo)
                .one();
    }

    /**
     * 处理退款失败
     */
    private void handleRefundFailure(RefundOrder refundOrder, Exception error) {
        String errorMessage = error != null ? error.getMessage() : "未知错误";
        rejectRefund(refundOrder, errorMessage);
        sendRefundNotification(refundOrder, "退款失败：" + errorMessage);
        throw new BusinessException("退款处理失败：" + errorMessage);
    }
}