package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.*;
import com.example.miracle.modules.company.entity.Order;
import com.example.miracle.modules.company.entity.OrderDelivery;
import com.example.miracle.modules.company.entity.OrderEvaluation;
import com.example.miracle.modules.company.entity.OrderNotification;
import com.example.miracle.modules.company.vo.OrderStatisticsVO;

import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletResponse;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Order createOrder(OrderCreateDTO dto, String operator);

    /**
     * 分页查询订单
     */
    Page<Order> pageOrder(Integer current, Integer size, Long merchantId,Long companyId,
                          String orderNo, Integer status,
                          LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取订单详情
     */
    Order getOrderDetail(Long id);

    /**
     * 取消订单
     */
    void cancelOrder(Long id, String cancelReason, String operator);

    /**
     * 发货
     */
    void deliver(Long id, String operator);

    /**
     * 完成订单
     */
    void complete(Long id, String operator);

    /**
     * 退款
     */
    void refund(Long id, String operator);


    /**
     * 处理支付回调
     */
    void handlePayCallback(OrderPayCallbackDTO dto);

    /**
     * 处理订单超时
     */
    void handleOrderTimeout();

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 目标状态
     * @param operator 操作人
     * @param remark 备注说明
     */
    void updateOrderStatus(Long orderId, Integer status, String operator, String remark);

    /**
     * 发货
     */
    void deliver(String orderNo, OrderDeliveryDTO dto);

    /**
     * 更新物流信息
     */
    void updateTracking(String orderNo, String trackingInfo);

    /**
     * 确认收货
     */
    void confirmReceive(String orderNo, String operator);



    /**
     * 取消订单
     */
    void cancelOrder(String orderNo, String reason, String operator);

    /**
     * 自动取消超时订单(定时任务调用)
     */
    void autoCancelTimeoutOrders();

    /**
     * 评价订单
     */
    void evaluate(OrderEvaluationDTO dto);

    /**
     * 商家回复评价
     */
    void replyEvaluation(Long evaluationId, String reply, String operator);

    /**
     * 导出订单
     * @param query 查询条件
     * @param response HTTP响应
     */
    void exportOrders(OrderQueryDTO query, HttpServletResponse response);

    /**
     * 打印订单
     * @param orderNo 订单编号
     */
    void printOrder(String orderNo);

    /**
     * 获取订单统计
     */
    OrderStatisticsVO getOrderStatistics(OrderStatisticsQuery query);
}
