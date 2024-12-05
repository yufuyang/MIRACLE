package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.OrderEvaluation;

import java.util.List;

public interface OrderEvaluationService extends IService<OrderEvaluation> {
    
    /**
     * 获取商品评价列表
     */
    List<OrderEvaluation> getProductEvaluations(Long productId);
    
    /**
     * 获取商品评分
     */
    Double getProductScore(Long productId);
    
    /**
     * 获取订单评价
     */
    OrderEvaluation getOrderEvaluation(Long orderId);
} 