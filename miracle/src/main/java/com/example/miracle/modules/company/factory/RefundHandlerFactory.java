package com.example.miracle.modules.company.factory;

import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.service.RefundHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RefundHandlerFactory {
    private final Map<Integer, RefundHandler> handlerMap = new HashMap<>();
    
    @Autowired
    public void init(List<RefundHandler> handlers) {
        for (RefundHandler handler : handlers) {
            handlerMap.put(handler.getRefundType(), handler);
        }
    }
    
    public RefundHandler getHandler(Integer refundType) {
        RefundHandler handler = handlerMap.get(refundType);
        if (handler == null) {
            throw new BusinessException("不支持的退款类型");
        }
        return handler;
    }
} 