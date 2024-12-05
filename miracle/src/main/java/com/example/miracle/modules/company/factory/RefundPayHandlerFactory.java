package com.example.miracle.modules.company.factory;

import com.example.miracle.modules.company.service.RefundPayHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefundPayHandlerFactory {
    private final Map<Integer, RefundPayHandler> handlerMap = new HashMap<>();
    
    @Autowired
    public void init(List<RefundPayHandler> handlers) {
        for (RefundPayHandler handler : handlers) {
            handlerMap.put(handler.getPayType(), handler);
        }
    }
    
    public Optional<RefundPayHandler> getHandler(Integer payType) {
        return Optional.ofNullable(handlerMap.get(payType));
    }
} 