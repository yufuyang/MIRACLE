package com.example.miracle.modules.company.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.ReturnStatusEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.RefundItemDTO;
import com.example.miracle.modules.company.dto.ReturnDetailDTO;
import com.example.miracle.modules.company.dto.ReturnLogisticsDTO;
import com.example.miracle.modules.company.dto.ReturnReceiveDTO;
import com.example.miracle.modules.company.entity.RefundOrder;
import com.example.miracle.modules.company.entity.ReturnItem;
import com.example.miracle.modules.company.entity.ReturnOrder;
import com.example.miracle.modules.company.mapper.ReturnOrderMapper;
import com.example.miracle.modules.company.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReturnOrderServiceImpl extends ServiceImpl<ReturnOrderMapper, ReturnOrder> implements ReturnOrderService {

    private final ReturnItemService returnItemService;
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final OrderService orderService;

    private final OrderLogService orderLogService;

    private final RefundOrderService refundOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrder createReturnOrder(RefundOrder refundOrder, List<RefundItemDTO> items) {
        // 1. 创建退货单
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setReturnNo(generateReturnNo());
        returnOrder.setRefundId(refundOrder.getId());
        returnOrder.setRefundNo(refundOrder.getRefundNo());
        returnOrder.setOrderId(refundOrder.getOrderId());
        returnOrder.setOrderNo(refundOrder.getOrderNo());
        returnOrder.setMerchantId(refundOrder.getMerchantId());
        returnOrder.setStatus(ReturnStatusEnum.PENDING.getCode());
        returnOrder.setOperator(refundOrder.getOperator());
        this.save(returnOrder);

        // 2. 创建退货商品
        returnItemService.createReturnItems(returnOrder.getId(), items);

        return returnOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReturnAddress(String refundNo, String returnAddress) {
        ReturnOrder returnOrder = this.getByRefundNo(refundNo);
        if (returnOrder == null) {
            throw new BusinessException("退货单不存在");
        }

        this.lambdaUpdate()
                .set(ReturnOrder::getReturnAddress, returnAddress)
                .eq(ReturnOrder::getId, returnOrder.getId())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitLogistics(ReturnLogisticsDTO dto) {
        // 1. 查询退货单
        ReturnOrder returnOrder = this.lambdaQuery()
                .eq(ReturnOrder::getReturnNo, dto.getReturnNo())
                .one();

        if (returnOrder == null) {
            throw new BusinessException("退货单不存在");
        }

        // 2. 校验退货单状态
        if (!ReturnStatusEnum.PENDING.getCode().equals(returnOrder.getStatus())) {
            throw new BusinessException("退货单状态不正确");
        }

        // 3. 更新退货单物流信息
        this.lambdaUpdate()
                .set(ReturnOrder::getLogisticsCompany, dto.getLogisticsCompany())
                .set(ReturnOrder::getLogisticsNo, dto.getLogisticsNo())
                .set(ReturnOrder::getStatus, ReturnStatusEnum.RETURNING.getCode())
                .eq(ReturnOrder::getId, returnOrder.getId())
                .update();

        // 4. 记录订单日志
        orderLogService.recordLog(returnOrder.getOrderId(), dto.getOperator(), "提交退货物流信息",
                String.format("物流公司：%s，物流单号：%s", dto.getLogisticsCompany(), dto.getLogisticsNo()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(ReturnReceiveDTO dto) {
        // 1. 查询退货单
        ReturnOrder returnOrder = this.lambdaQuery()
                .eq(ReturnOrder::getReturnNo, dto.getReturnNo())
                .one();

        if (returnOrder == null) {
            throw new BusinessException("退货单不存在");
        }

        // 2. 校验退货单状态
        if (!ReturnStatusEnum.RETURNING.getCode().equals(returnOrder.getStatus())) {
            throw new BusinessException("退货单状态不正确");
        }

        // 3. 更新退货单状态
        this.lambdaUpdate()
                .set(ReturnOrder::getStatus, ReturnStatusEnum.RECEIVED.getCode())
                .set(ReturnOrder::getReceiveTime, LocalDateTime.now())
                .set(ReturnOrder::getReceiveUser, dto.getOperator())
                .set(ReturnOrder::getReceiveRemark, dto.getReceiveRemark())
                .eq(ReturnOrder::getId, returnOrder.getId())
                .update();

        // 4. 恢复商品库存
        List<ReturnItem> returnItems = returnItemService.getReturnItems(returnOrder.getId());
        for (ReturnItem item : returnItems) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }

        // 5. 处理退款
        refundOrderService.processRefund(returnOrder.getRefundNo());

        // 6. 记录订单日志
        orderLogService.recordLog(returnOrder.getOrderId(), dto.getOperator(), "确认收货", dto.getReceiveRemark());
    }

    @Override
    public int getRefundedQuantity(Long orderItemId) {
        // 查询已退货数量（状态为已收货的退货单）
        List<ReturnItem> returnItems = returnItemService.lambdaQuery()
                .eq(ReturnItem::getOrderItemId, orderItemId)
                .list();

        if (CollectionUtils.isEmpty(returnItems)) {
            return 0;
        }

        // 只统计已收货的退货单中的数量
        return returnItems.stream()
                .filter(item -> {
                    ReturnOrder returnOrder = this.getById(item.getReturnId());
                    return returnOrder != null &&
                            ReturnStatusEnum.RECEIVED.getCode().equals(returnOrder.getStatus());
                })
                .mapToInt(ReturnItem::getQuantity)
                .sum();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReturn(String refundNo) {
        ReturnOrder returnOrder = getByRefundNo(refundNo);
        if (returnOrder == null) {
            throw new BusinessException("退货单不存在");
        }

        // 校验状态
        if (!ReturnStatusEnum.PENDING.getCode().equals(returnOrder.getStatus())) {
            throw new BusinessException("只能取消待审核的退货单");
        }

        // 更新状态
        this.lambdaUpdate()
                .set(ReturnOrder::getStatus, ReturnStatusEnum.CANCELLED.getCode())
                .eq(ReturnOrder::getId, returnOrder.getId())
                .update();
    }

    @Override
    public ReturnDetailDTO getReturnDetail(String returnNo) {
        // 1. 查询退货单
        ReturnOrder returnOrder = this.lambdaQuery()
                .eq(ReturnOrder::getReturnNo, returnNo)
                .one();

        if (returnOrder == null) {
            throw new BusinessException("退货单不存在");
        }

        // 2. 构建详情VO
        ReturnDetailDTO vo = new ReturnDetailDTO();
        BeanUtils.copyProperties(returnOrder, vo);

        // 3. 查询退货商品
        List<ReturnItem> items = returnItemService.getReturnItems(returnOrder.getId());
        vo.setItems(items);

        return vo;
    }

    @Override
    public ReturnOrder getByRefundNo(String refundNo) {
        return this.lambdaQuery()
                .eq(ReturnOrder::getRefundNo, refundNo)
                .one();
    }

    @Override
    public List<RefundItemDTO> getRefundItems(String refundNo) {
        ReturnOrder returnOrder = getByRefundNo(refundNo);
        if (returnOrder == null) {
            return Collections.emptyList();
        }

        // 查询退货商品
        List<ReturnItem> items = returnItemService.lambdaQuery()
                .eq(ReturnItem::getReturnId, returnOrder.getId())
                .list();

        // 转换为DTO
        return items.stream().map(item -> {
            RefundItemDTO dto = new RefundItemDTO();
            dto.setOrderItemId(item.getOrderItemId());
            dto.setQuantity(item.getQuantity());
            dto.setRefundAmount(item.getRefundAmount());
            dto.setReason(item.getReason());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 生成退货单号
     */
    private String generateReturnNo() {
        return "RT" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + RandomUtil.randomNumbers(6);
    }
}