package com.example.miracle.modules.company.task;

import com.example.miracle.common.constant.OrderStatusEnum;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAutoConfirmReceiveTask {

    @Resource
    private OrderService orderService;


    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void autoConfirmReceive() {
        // 查询15天前发货的订单
        LocalDateTime confirmTime = LocalDateTime.now().minusDays(15);
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getStatus, OrderStatusEnum.DELIVERED.getCode())
                .le(Order::getDeliveryTime, confirmTime)
                .list();

        for (Order order : orders) {
            try {
                orderService.confirmReceive(order.getOrderNo(), "SYSTEM");
            } catch (Exception e) {
                log.error("自动确认收货失败，订单号：{}，原因：{}",
                        order.getOrderNo(), e.getMessage());
            }
        }
    }

}
