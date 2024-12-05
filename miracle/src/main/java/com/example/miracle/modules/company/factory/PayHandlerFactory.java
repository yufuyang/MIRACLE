package com.example.miracle.modules.company.factory;

import com.example.miracle.modules.company.service.PayHandler;
import com.example.miracle.modules.company.service.impl.WxPayHandler;
import com.example.miracle.modules.company.service.impl.AliPayHandler;
import com.example.miracle.modules.company.service.impl.CashPayHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PayHandlerFactory {
    private final WxPayHandler wxPayHandler;
    private final AliPayHandler aliPayHandler;
    private final CashPayHandler cashPayHandler;
    
    private final Map<Integer, PayHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        handlerMap.put(1, wxPayHandler);
        handlerMap.put(2, aliPayHandler);
        handlerMap.put(3, cashPayHandler);
    }

    public Optional<PayHandler> getHandler(Integer payType) {
        return Optional.ofNullable(handlerMap.get(payType));
    }
} 