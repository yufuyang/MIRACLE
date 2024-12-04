package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.ProductStockLog;
import com.example.miracle.modules.company.mapper.ProductStockLogMapper;
import com.example.miracle.modules.company.service.ProductStockLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductStockLogServiceImpl extends ServiceImpl<ProductStockLogMapper, ProductStockLog> implements ProductStockLogService {
}