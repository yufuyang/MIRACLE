package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.OrderDelivery;
import com.example.miracle.modules.company.mapper.OrderDeliveryMapper;
import com.example.miracle.modules.company.service.OrderDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDeliveryServiceImpl extends ServiceImpl<OrderDeliveryMapper, OrderDelivery> implements OrderDeliveryService {
    
    @Override
    public OrderDelivery getByOrderNo(String orderNo) {
        return this.lambdaQuery()
                .eq(OrderDelivery::getOrderNo, orderNo)
                .one();
    }
    
    @Override
    public void updateDeliveryStatus(Long id, Integer status) {
        this.lambdaUpdate()
                .set(OrderDelivery::getStatus, status)
                .eq(OrderDelivery::getId, id)
                .update();
    }
} 