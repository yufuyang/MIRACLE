package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
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
public class CompanyOrderController {

    private final OrderService orderService;

    /**
     * 支付回调
     */
    @PostMapping("/pay/callback")
    public ResultDTO<?> payCallback(@RequestBody OrderPayCallbackDTO dto) {
        orderService.handlePayCallback(dto);
        return ResultDTO.ok();
    }
    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public ResultDTO<Page<Order>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestAttribute Long companyId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return ResultDTO.ok(orderService.pageOrder(current, size,merchantId, companyId, orderNo, status, startTime, endTime));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ResultDTO<Order> detail(@PathVariable Long id) {
        return ResultDTO.ok(orderService.getOrderDetail(id));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public ResultDTO<?> cancel(
            @PathVariable Long id,
            @RequestParam String cancelReason,
            @RequestAttribute String operator) {
        orderService.cancelOrder(id, cancelReason, operator);
        return ResultDTO.ok();
    }

    /**
     * 发货
     */
    @PutMapping("/{id}/deliver")
    public ResultDTO<?> deliver(
            @PathVariable Long id,
            @RequestAttribute String operator) {
        orderService.deliver(id, operator);
        return ResultDTO.ok();
    }

    /**
     * 完成订单
     */
    @PutMapping("/{id}/complete")
    public ResultDTO<?> complete(
            @PathVariable Long id,
            @RequestAttribute String operator) {
        orderService.complete(id, operator);
        return ResultDTO.ok();
    }

    /**
     * 退款
     */
    @PutMapping("/{id}/refund")
    public ResultDTO<?> refund(
            @PathVariable Long id,
            @RequestAttribute String operator) {
        orderService.refund(id, operator);
        return ResultDTO.ok();
    }
}