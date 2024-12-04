package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.OrderLog;
import com.example.miracle.modules.company.mapper.OrderLogMapper;
import com.example.miracle.modules.company.service.OrderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderLogServiceImpl extends ServiceImpl<OrderLogMapper, OrderLog> implements OrderLogService {

    @Override
    public void recordLog(Long orderId, String operator, String action, String content) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperator(operator);
        log.setAction(action);
        log.setContent(content);
        this.save(log);
    }
}