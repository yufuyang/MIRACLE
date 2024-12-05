package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.OrderEvaluation;
import com.example.miracle.modules.company.mapper.OrderEvaluationMapper;
import com.example.miracle.modules.company.service.OrderEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderEvaluationServiceImpl extends ServiceImpl<OrderEvaluationMapper, OrderEvaluation> implements OrderEvaluationService {
    
    @Override
    public List<OrderEvaluation> getProductEvaluations(Long productId) {
        return this.lambdaQuery()
                .eq(OrderEvaluation::getProductId, productId)
                .orderByDesc(OrderEvaluation::getCreateTime)
                .list();
    }
    
    @Override
    public Double getProductScore(Long productId) {
        List<OrderEvaluation> evaluations = getProductEvaluations(productId);
        if (evaluations.isEmpty()) {
            return 5.0; // 默认5分
        }
        
        double totalScore = evaluations.stream()
                .mapToInt(OrderEvaluation::getScore)
                .sum();
        return totalScore / evaluations.size();
    }
    
    @Override
    public OrderEvaluation getOrderEvaluation(Long orderId) {
        return this.lambdaQuery()
                .eq(OrderEvaluation::getOrderId, orderId)
                .one();
    }
} 