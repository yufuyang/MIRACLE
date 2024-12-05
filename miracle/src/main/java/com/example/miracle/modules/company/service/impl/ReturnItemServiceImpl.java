package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.RefundItemDTO;
import com.example.miracle.modules.company.entity.OrderItem;
import com.example.miracle.modules.company.entity.ReturnItem;
import com.example.miracle.modules.company.mapper.ReturnItemMapper;
import com.example.miracle.modules.company.service.OrderItemService;
import com.example.miracle.modules.company.service.ReturnItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnItemServiceImpl extends ServiceImpl<ReturnItemMapper, ReturnItem> implements ReturnItemService {

    private final OrderItemService orderItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReturnItems(Long returnId, List<RefundItemDTO> items) {
        for (RefundItemDTO item : items) {
            // 1. 查询订单商品
            OrderItem orderItem = orderItemService.getById(item.getOrderItemId());
            if (orderItem == null) {
                throw new BusinessException("订单商品不存在");
            }

            // 2. 创建退货商品
            ReturnItem returnItem = new ReturnItem();
            returnItem.setReturnId(returnId);
            returnItem.setOrderItemId(orderItem.getId());
            returnItem.setProductId(orderItem.getProductId());
            returnItem.setProductName(orderItem.getProductName());
            returnItem.setQuantity(item.getQuantity());
            returnItem.setPrice(orderItem.getPrice());
            returnItem.setTotalAmount(orderItem.getPrice().multiply(new BigDecimal(item.getQuantity())));
            this.save(returnItem);
        }
    }

    @Override
    public List<ReturnItem> getReturnItems(Long returnId) {
        return this.lambdaQuery()
                .eq(ReturnItem::getReturnId, returnId)
                .list();
    }
}
