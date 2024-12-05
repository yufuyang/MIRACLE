package com.example.miracle.modules.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.dto.OrderCreateDTO;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/merchant/order")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderService orderService;
    /**
     * 创建订单
     */
    @PostMapping
    public ResultDTO<Order> create(@RequestBody OrderCreateDTO orderCreateDTO, @RequestAttribute String operator) {
        return ResultDTO.ok(orderService.createOrder(orderCreateDTO, operator));
    }


    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public ResultDTO<Page<Order>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long companyId,
            @RequestAttribute Long merchantId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return ResultDTO.ok(orderService.pageOrder(current, size,merchantId, companyId, orderNo, status, startTime, endTime));
    }
}
