package com.example.miracle.modules.company.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.OrderLog;

public interface OrderLogService extends IService<OrderLog> {

    /**
     * 记录订单日志
     */
    void recordLog(Long orderId, String operator, String action, String content);
}