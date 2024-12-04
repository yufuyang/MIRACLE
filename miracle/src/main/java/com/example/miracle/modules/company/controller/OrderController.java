package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.company.dto.OrderCreateDTO;
import com.example.miracle.modules.company.dto.OrderPayCallbackDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/company/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    /**
     * 创建订单
     */
    @PostMapping
    public Result<Order> create(@RequestBody OrderCreateDTO dto, @RequestAttribute String operator) {
        return Result.ok(orderService.createOrder(dto, operator));
    }
    /**
     * 支付回调
     */
    @PostMapping("/pay/callback")
    public Result<?> payCallback(@RequestBody OrderPayCallbackDTO dto) {
        orderService.handlePayCallback(dto);
        return Result.ok();
    }
    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public Result<Page<Order>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long merchantId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.ok(orderService.pageOrder(current, size, merchantId, orderNo, status, startTime, endTime));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(
            @PathVariable Long id,
            @RequestParam String cancelReason,
            @RequestAttribute String operator) {
        orderService.cancelOrder(id, cancelReason, operator);
        return Result.ok();
    }

    /**
     * 发货
     */
    @PutMapping("/{id}/deliver")
    public Result<?> deliver(
            @PathVariable Long id,
            @RequestAttribute String operator) {
        orderService.deliver(id, operator);
        return Result.ok();
    }

    /**
     * 完成订单
     */
    @PutMapping("/{id}/complete")
    public Result<?> complete(
            @PathVariable Long id,
            @RequestAttribute String operator) {
        orderService.complete(id, operator);
        return Result.ok();
    }

    /**
     * 退款
     */
    @PutMapping("/{id}/refund")
    public Result<?> refund(
            @PathVariable Long id,
            @RequestAttribute String operator) {
        orderService.refund(id, operator);
        return Result.ok();
    }
}