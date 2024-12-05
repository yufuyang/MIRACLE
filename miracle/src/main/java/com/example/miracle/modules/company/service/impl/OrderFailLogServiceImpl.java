package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.OrderFailLog;
import com.example.miracle.modules.company.mapper.OrderFailLogMapper;
import com.example.miracle.modules.company.service.OrderFailLogService;
import org.springframework.stereotype.Service;

@Service
public class OrderFailLogServiceImpl extends ServiceImpl<OrderFailLogMapper, OrderFailLog> implements OrderFailLogService {
} 