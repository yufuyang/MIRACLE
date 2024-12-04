package com.example.miracle.modules.company.task;

import com.example.miracle.modules.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;

    /**
     * 每分钟执行一次订单超时检查
     */
    @Scheduled(cron = "0 * * * * ?")
    public void handleOrderTimeout() {
        try {
            orderService.handleOrderTimeout();
        } catch (Exception e) {
            log.error("订单超时处理失败", e);
        }
    }
}