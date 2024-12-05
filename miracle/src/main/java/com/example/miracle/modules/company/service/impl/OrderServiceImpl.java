package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.OrderStatusEnum;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.*;
import com.example.miracle.modules.company.entity.*;
import com.example.miracle.modules.company.mapper.OrderMapper;
import com.example.miracle.modules.company.service.*;
import com.example.miracle.modules.company.vo.OrderStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemService orderItemService;
    private final OrderLogService orderLogService;
    private final ProductService productService;
    private final OrderPaymentService orderPaymentService;
    private final PayMethodConfigService payMethodConfigService;
    private final PayService payService;
    private final OrderFailLogService orderFailLogService;
    private final OrderDeliveryService orderDeliveryService;
    private final OrderEvaluationService orderEvaluationService;
    private final OrderNotificationService orderNotificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderCreateDTO dto, String operator) {
        // 1. 校验并获取商品信息
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : dto.getOrderItems()) {

            Product product = productService.getById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            if (product.getStatus() != 1) {
                throw new BusinessException("商品已下架");
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("商品库存不足");
            }

            // 创建订单商品
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalAmount(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalAmount());

            // 扣减库存
            productService.deductStock(product.getId(), itemDTO.getQuantity());
        }

        // 校验支付方式
        PayMethodConfig payMethod = payMethodConfigService.getById(dto.getPayMethodId());
        if (payMethod == null || !payMethod.getStatus().equals(1)) {
            throw new BusinessException("支付方式未配置或已禁用");
        }

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo()); // 生成订单编号
        order.setMerchantId(dto.getMerchantId());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 实际支付金额，可以根据需求处理优惠等
        order.setStatus(OrderStatusEnum.UNPAID.getCode()); // 待支付
        order.setRemark(dto.getRemark());
        order.setPayMethodId(payMethod.getId());
        order.setPayType(payMethod.getPayType());
        order.setPayMethodName(payMethod.getMethodName());

        this.save(order);

        // 3. 保存订单商品
        orderItemService.saveOrderItems(order.getId(), orderItems);

        // 4. 记录订单日志
        orderLogService.recordLog(order.getId(), operator, "创建订单",
                String.format("订单金额：%s，商品数量：%d", totalAmount, orderItems.size()));

        return this.getOrderDetail(order.getId());
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        // 示例：年月日时分秒+6位随机数
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", new Random().nextInt(1000000));
    }

    @Override
    public Page<Order> pageOrder(Integer current, Integer size, Long merchantId, Long companyId,
                                 String orderNo, Integer status,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(companyId != null, Order::getCompanyId, companyId)
                .eq(merchantId != null, Order::getMerchantId, merchantId)
                .eq(StringUtils.hasText(orderNo), Order::getOrderNo, orderNo)
                .eq(status != null, Order::getStatus, status)
                .ge(startTime != null, Order::getCreateTime, startTime)
                .le(endTime != null, Order::getCreateTime, endTime)
                .orderByDesc(Order::getCreateTime);

        // 执行分页查询
        Page<Order> page = this.page(new Page<>(current, size), wrapper);

        // 填充订单商品信息
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            for (Order order : page.getRecords()) {
                List<OrderItem> orderItems = orderItemService.getOrderItems(order.getId());
                order.setOrderItems(orderItems);
            }
        }

        return page;
    }

    @Override
    public Order getOrderDetail(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 获取订单商品
        List<OrderItem> orderItems = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, id)
                .list();
        order.setOrderItems(orderItems);

        // 获取订单日志
        List<OrderLog> orderLogs = orderLogService.lambdaQuery()
                .eq(OrderLog::getOrderId, id)
                .orderByDesc(OrderLog::getCreateTime)
                .list();
        order.setOrderLogs(orderLogs);

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id, String cancelReason, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 更新状态
        updateOrderStatus(id, OrderStatusEnum.CANCELLED.getCode(), operator, cancelReason);

        // 其他取消逻辑（如恢复库存等）
        restoreStock(order);
        // 关闭支付
        payService.closePayment(order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long id, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验订单状态
        if (!OrderStatusEnum.PAID.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 更新订单状态
        updateOrderStatus(id, OrderStatusEnum.DELIVERED.getCode(), operator, "订单发货");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验订单状态
        if (!OrderStatusEnum.DELIVERED.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能完成已发货订单");
        }

        // 更新订单状态
        updateOrderStatus(id, OrderStatusEnum.COMPLETED.getCode(), operator, "订单完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long id, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验订单状态
        if (!OrderStatusEnum.canRefund(order.getStatus())) {
            throw new BusinessException("当前订单状态不可退款");
        }

        // 更新订单状态
        updateOrderStatus(id, OrderStatusEnum.REFUNDED.getCode(), operator, "订单退款");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePayCallback(OrderPayCallbackDTO dto) {
        // 1. 查询订单
        Order order = this.lambdaQuery()
                .eq(Order::getOrderNo, dto.getOrderNo())
                .one();

        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (order.getStatus() != OrderStatusEnum.UNPAID.getCode()) {
            throw new BusinessException("订单状态不正确");
        }

        // 3. 校验支付金额
        if (order.getPayAmount().compareTo(dto.getPayAmount()) != 0) {
            throw new BusinessException("支付金额不正确");
        }

        // 4. 保存支付记录
        OrderPayment payment = new OrderPayment();
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setPayType(dto.getPayType());
        payment.setPayAmount(dto.getPayAmount());
        payment.setTradeNo(dto.getTradeNo());
        orderPaymentService.createPayment(payment);

        // 5. 更新订单状态
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPayType(dto.getPayType());
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);

        // 6. 记录订单日志
        orderLogService.recordLog(order.getId(), "SYSTEM", "支付成功", String.format("支付方式：%s，支付金额：%s，流水号：%s", dto.getPayType(), dto.getPayAmount(), dto.getTradeNo()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderTimeout() {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);
        int pageSize = 100;
        int current = 1;
        
        while (true) {
            // 分页查询超时订单
            Page<Order> page = this.lambdaQuery()
                    .eq(Order::getStatus, OrderStatusEnum.UNPAID.getCode())
                    .le(Order::getCreateTime, timeout)
                    .page(new Page<>(current, pageSize));
                    
            if (page.getRecords().isEmpty()) {
                break;
            }
            
            // 批量处理订单
            for (Order order : page.getRecords()) {
                try {
                    handleTimeoutOrder(order);
                } catch (Exception e) {
                    log.error("处理超时订单失败，订单号：{}，原因：{}", order.getOrderNo(), e.getMessage(), e);
                    // 记录失败订单，后续补偿处理
                    recordFailedOrder(order.getId(), "超时订单处理失败", e.getMessage());
                }
            }
            
            if (!page.hasNext()) {
                break;
            }
            current++;
        }
    }

    /**
     * 处理单个超时订单
     */
    private void handleTimeoutOrder(Order order) {
        // 更新订单状态
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason("订单超时自动取消");
        this.updateById(order);

        // 恢复商品库存
        List<OrderItem> orderItems = orderItemService.getOrderItems(order.getId());
        for (OrderItem item : orderItems) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }

        // 记录订单日志
        orderLogService.recordLog(order.getId(), "SYSTEM", "订单自动取消", "超时未支付，系统自动取消");
    }

    /**
     * 记录失败订单
     */
    private void recordFailedOrder(Long orderId, String type, String reason) {
        OrderFailLog failLog = new OrderFailLog();
        failLog.setOrderId(orderId);
        failLog.setType(type);
        failLog.setReason(reason);
        failLog.setStatus(0);
        failLog.setCreateTime(LocalDateTime.now());
        orderFailLogService.save(failLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long orderId, Integer status, String operator, String remark) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验状态转换
        if (!OrderStatusEnum.canTransfer(order.getStatus(), status)) {
            throw new BusinessException("订单状态转换非法");
        }

        // 更新状态
        order.setStatus(status);
        this.updateById(order);

        // 记录日志
        String content = String.format("状态从[%s]变更为[%s]",
                OrderStatusEnum.getByCode(order.getStatus()).getDesc(),
                OrderStatusEnum.getByCode(status).getDesc());
        if (StringUtils.hasLength(remark)) {
            content += "，备注：" + remark;
        }
        orderLogService.recordLog(orderId, operator, "更新状态", content);
    }

    private void restoreStock(Order order) {
        List<OrderItem> orderItems = orderItemService.getOrderItems(order.getId());
        for (OrderItem item : orderItems) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(String orderNo, OrderDeliveryDTO dto) {
        // 1. 查询订单
        Order order = this.lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .one();
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (!OrderStatusEnum.PAID.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 3. 创建发货记录
        OrderDelivery delivery = new OrderDelivery();
        delivery.setOrderId(order.getId());
        delivery.setOrderNo(orderNo);
        delivery.setExpressCompany(dto.getExpressCompany());
        delivery.setExpressNo(dto.getExpressNo());
        delivery.setReceiverName(dto.getReceiverName());
        delivery.setReceiverPhone(dto.getReceiverPhone());
        delivery.setReceiverAddress(dto.getReceiverAddress());
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setStatus(1);
        orderDeliveryService.save(delivery);

        // 4. 更新订单状态
        updateOrderStatus(order.getId(), OrderStatusEnum.DELIVERED.getCode(), 
            dto.getOperator(), "订单发货");

        // 5. 创建发货通知
        OrderNotification notification = new OrderNotification();
        notification.setOrderId(order.getId());
        notification.setOrderNo(orderNo);
        notification.setType(2);
        notification.setContent(String.format("您的订单已发货，物流公司：%s，物流单号：%s", 
            dto.getExpressCompany(), dto.getExpressNo()));
        notification.setStatus(0);
        orderNotificationService.save(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTracking(String orderNo, String trackingInfo) {
        OrderDelivery delivery = orderDeliveryService.lambdaQuery()
                .eq(OrderDelivery::getOrderNo, orderNo)
                .one();
        if (delivery != null) {
            delivery.setTrackingInfo(trackingInfo);
            orderDeliveryService.updateById(delivery);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(String orderNo, String operator) {
        // 1. 查询订单
        Order order = this.lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .one();
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (!OrderStatusEnum.DELIVERED.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 3. 更新发货记录
        OrderDelivery delivery = orderDeliveryService.lambdaQuery()
                .eq(OrderDelivery::getOrderNo, orderNo)
                .one();
        if (delivery != null) {
            delivery.setStatus(2);
            delivery.setReceiveTime(LocalDateTime.now());
            orderDeliveryService.updateById(delivery);
        }

        // 4. 更新订单状态
        updateOrderStatus(order.getId(), OrderStatusEnum.COMPLETED.getCode(), 
            operator, "确认收货");

        // 5. 更新商品销量
        List<OrderItem> items = orderItemService.getOrderItems(order.getId());
        for (OrderItem item : items) {
            productService.increaseSales(item.getProductId(), item.getQuantity());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluate(OrderEvaluationDTO dto) {
        // 1. 查询订单
        Order order = this.lambdaQuery()
                .eq(Order::getOrderNo, dto.getOrderNo())
                .one();
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (!OrderStatusEnum.COMPLETED.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能评价已完成的订单");
        }

        // 3. 校验是否已评价
        boolean exists = orderEvaluationService.lambdaQuery()
                .eq(OrderEvaluation::getOrderId, order.getId())
                .exists();
        if (exists) {
            throw new BusinessException("订单已评价");
        }

        // 4. 创建评价
        OrderEvaluation evaluation = new OrderEvaluation();
        evaluation.setOrderId(order.getId());
        evaluation.setOrderNo(order.getOrderNo());
        evaluation.setProductId(dto.getProductId());
        evaluation.setScore(dto.getScore());
        evaluation.setContent(dto.getContent());
        evaluation.setImages(String.join(",", dto.getImages()));
        evaluation.setAnonymous(dto.getAnonymous());
        orderEvaluationService.save(evaluation);

        // 5. 记录订单日志
        orderLogService.recordLog(order.getId(), "USER", "订单评价", 
            String.format("评分：%d，评价内容：%s", dto.getScore(), dto.getContent()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyEvaluation(Long evaluationId, String reply, String operator) {
        // 1. 查询评价
        OrderEvaluation evaluation = orderEvaluationService.getById(evaluationId);
        if (evaluation == null) {
            throw new BusinessException("评价不存在");
        }

        // 2. 更新评价
        evaluation.setReply(reply);
        evaluation.setReplyTime(LocalDateTime.now());
        orderEvaluationService.updateById(evaluation);

        // 3. 记录订单日志
        orderLogService.recordLog(evaluation.getOrderId(), operator, "回复评价", reply);
    }

    @Override
    public OrderStatisticsVO getOrderStatistics(OrderStatisticsQuery query) {
        OrderStatisticsVO vo = new OrderStatisticsVO();
        
        // 1. 查询订单总数和总金额
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getMerchantId, query.getMerchantId())
                .ge(query.getStartTime() != null, Order::getCreateTime, query.getStartTime())
                .le(query.getEndTime() != null, Order::getCreateTime, query.getEndTime());
        
        List<Order> orders = this.list(wrapper);
        
        vo.setTotalCount(orders.size());
        vo.setTotalAmount(orders.stream()
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // 2. 按时间维度统计
        final Map<String, List<Order>> groupByDate = new HashMap<>();
        DateTimeFormatter formatter;
        switch (query.getType()) {
            case 1: // 按天
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                groupByDate.putAll(orders.stream()
                        .collect(Collectors.groupingBy(order -> 
                            order.getCreateTime().format(formatter))));
                break;
            case 2: // 按月
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                groupByDate.putAll(orders.stream()
                        .collect(Collectors.groupingBy(order -> 
                            order.getCreateTime().format(formatter))));
                break;
            case 3: // 按年
                formatter = DateTimeFormatter.ofPattern("yyyy");
                groupByDate.putAll(orders.stream()
                        .collect(Collectors.groupingBy(order -> 
                            order.getCreateTime().format(formatter))));
                break;
            default:
                throw new BusinessException("不支持的统计类型");
        }

        // 3. 组装统计数据
        List<String> dateList = new ArrayList<>(groupByDate.keySet());
        Collections.sort(dateList);
        
        List<Integer> orderCountList = dateList.stream()
                .map(date -> groupByDate.get(date).size())
                .collect(Collectors.toList());
        
        List<BigDecimal> orderAmountList = dateList.stream()
                .map(date -> groupByDate.get(date).stream()
                        .map(Order::getPayAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .collect(Collectors.toList());

        vo.setDateList(dateList);
        vo.setOrderCountList(orderCountList);
        vo.setOrderAmountList(orderAmountList);
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, String reason, String operator) {
        // 1. 查询订单
        Order order = this.lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .one();
        if (order == null) {
            throw new BusinessException("订��不存在");
        }

        // 2. 校验订单状态
        if (!OrderStatusEnum.UNPAID.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能取消未支付的订单");
        }

        // 3. 更新订单状态
        updateOrderStatus(order.getId(), OrderStatusEnum.CANCELLED.getCode(), operator, reason);

        // 4. 恢复商品库存
        List<OrderItem> orderItems = orderItemService.getOrderItems(order.getId());
        for (OrderItem item : orderItems) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }
    }

    @Override
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行一次
    public void autoCancelTimeoutOrders() {
        // 查询30分钟前创建的未支付订单
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);
        List<Order> orders = this.lambdaQuery()
                .eq(Order::getStatus, OrderStatusEnum.UNPAID.getCode())
                .le(Order::getCreateTime, timeout)
                .list();

        for (Order order : orders) {
            try {
                cancelOrder(order.getOrderNo(), "超时未支付，系统自动取消", "SYSTEM");
            } catch (Exception e) {
                log.error("自动取消订单失败，订单号：{}，原因：{}", 
                    order.getOrderNo(), e.getMessage());
            }
        }
    }

    @Override
    public void exportOrders(OrderQueryDTO query, HttpServletResponse response) {
        try {
            // 1. 查询订单数据
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                    .eq(query.getMerchantId() != null, Order::getMerchantId, query.getMerchantId())
                    .eq(StringUtils.hasText(query.getOrderNo()), Order::getOrderNo, query.getOrderNo())
                    .eq(query.getStatus() != null, Order::getStatus, query.getStatus())
                    .ge(query.getStartTime() != null, Order::getCreateTime, query.getStartTime())
                    .le(query.getEndTime() != null, Order::getCreateTime, query.getEndTime())
                    .orderByDesc(Order::getCreateTime);

            List<Order> orders = this.list(wrapper);

            // 2. 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("订单列表");

            // 3. 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"订单编号", "商户ID", "订单金额", "支付金额", "订单状态", "支付方式", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 4. 填充数据
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getOrderNo());
                row.createCell(1).setCellValue(order.getMerchantId());
                row.createCell(2).setCellValue(order.getTotalAmount().toString());
                row.createCell(3).setCellValue(order.getPayAmount().toString());
                row.createCell(4).setCellValue(OrderStatusEnum.getByCode(order.getStatus()).getDesc());
                row.createCell(5).setCellValue(order.getPayMethodName());
                row.createCell(6).setCellValue(order.getCreateTime().toString());
            }

            // 5. 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");

            // 6. 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            log.error("导出订单失败", e);
            throw new BusinessException("导出订单失败：" + e.getMessage());
        }
    }

    @Override
    public void printOrder(String orderNo) {
        // 1. 查询订单信息
        Order order = this.lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .one();
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 查询订单商品
        List<OrderItem> items = orderItemService.getOrderItems(order.getId());

        // 3. 查询发货信息
        OrderDelivery delivery = orderDeliveryService.getByOrderNo(orderNo);

        // 4. 生成打印数据
        Map<String, Object> printData = new HashMap<>();
        printData.put("order", order);
        printData.put("items", items);
        printData.put("delivery", delivery);

        // TODO: 调用打印服务
        throw new BusinessException("打印功能开发中");
    }

}