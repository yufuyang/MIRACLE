package com.example.miracle.modules.company.service.impl;

import com.example.miracle.common.constant.ReturnStatusEnum;
import com.example.miracle.common.constant.RefundTypeEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.RefundItemDTO;
import com.example.miracle.modules.company.entity.RefundOrder;
import com.example.miracle.modules.company.entity.ReturnOrder;
import com.example.miracle.modules.company.entity.OrderItem;
import com.example.miracle.modules.company.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnRefundHandler implements RefundHandler {
    
    private final OrderService orderService;
    private final PayService payService;
    private final ReturnOrderService returnOrderService;
    private final OrderItemService orderItemService;
    
    @Override
    public void handleRefund(RefundOrder refundOrder) {
        // 1. 验证退货状态
        ReturnOrder returnOrder = returnOrderService.getByRefundNo(refundOrder.getRefundNo());
        if (returnOrder == null || !ReturnStatusEnum.RECEIVED.equals(returnOrder.getStatus())) {
            throw new BusinessException("退货未完成，不能退款");
        }
        
        // 2. 调用支付平台退款接口
        payService.refund(refundOrder.getOrderNo(), refundOrder.getRefundAmount());
    }
    
    @Override
    public void validateRefund(RefundOrder refundOrder) {
        // 验证退货商品
        List<RefundItemDTO> items = returnOrderService.getRefundItems(refundOrder.getRefundNo());
        validateRefundItems(refundOrder.getOrderId(), items);
    }
    
    @Override
    public Integer getRefundType() {
        return RefundTypeEnum.RETURN_REFUND.getCode();
    }

    /**
     * 校验退货商品
     */
    private void validateRefundItems(Long orderId, List<RefundItemDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            throw new BusinessException("请选择退货商品");
        }

        // 1. 查询订单商品
        List<OrderItem> orderItems = orderItemService.getOrderItems(orderId);
        Map<Long, OrderItem> orderItemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItem::getId, item -> item));

        // 2. 校验退货数量
        for (RefundItemDTO item : items) {
            OrderItem orderItem = orderItemMap.get(item.getOrderItemId());
            if (orderItem == null) {
                throw new BusinessException("订单商品不存在");
            }

            // 查询已退货数量
            int refundedQuantity = returnOrderService.getRefundedQuantity(orderItem.getId());

            // 校验退货数量
            if (item.getQuantity() > orderItem.getQuantity() - refundedQuantity) {
                throw new BusinessException(String.format("商品[%s]退货数量超过可退数量",
                        orderItem.getProductName()));
            }

            // 校验退款金额
            if (item.getRefundAmount().compareTo(orderItem.getPayAmount()) > 0) {
                throw new BusinessException(String.format("商品[%s]退款金额超过支付金额",
                        orderItem.getProductName()));
            }
        }
    }
} 