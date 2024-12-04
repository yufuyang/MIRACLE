package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.OrderCreateDTO;
import com.example.miracle.modules.company.dto.OrderItemDTO;
import com.example.miracle.modules.company.dto.OrderPayCallbackDTO;
import com.example.miracle.modules.company.entity.*;
import com.example.miracle.modules.company.mapper.OrderMapper;
import com.example.miracle.modules.company.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemService orderItemService;
    private final OrderLogService orderLogService;
    private final ProductService productService;
    private final OrderPaymentService orderPaymentService;
    private final PayMethodConfigService payMethodConfigService;
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

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo()); // 生成订单编号
        order.setMerchantId(dto.getMerchantId());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 实际支付金额，可以根据需求处理优惠等
        order.setStatus(0); // 待支付
        order.setRemark(dto.getRemark());

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
    public Page<Order> pageOrder(Integer current, Integer size, Long merchantId,
                                 String orderNo, Integer status,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getMerchantId, merchantId)
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

        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待支付订单");
        }

        // 更新订单状态
        order.setStatus(4);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(cancelReason);
        this.updateById(order);

        // 记录日志
        orderLogService.recordLog(id, operator, "取消订单", "取消原因：" + cancelReason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long id, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 1) {
            throw new BusinessException("只能发货已支付订单");
        }

        // 更新订单状态
        order.setStatus(2);
        order.setDeliveryTime(LocalDateTime.now());
        this.updateById(order);

        // 记录日志
        orderLogService.recordLog(id, operator, "订单发货", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 2) {
            throw new BusinessException("只能完成已发货订单");
        }

        // 更新订单状态
        order.setStatus(3);
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);

        // 记录日志
        orderLogService.recordLog(id, operator, "完成订单", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long id, String operator) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException("只能退款已支付或已发货订单");
        }

        // 更新订单状态
        order.setStatus(5);
        this.updateById(order);

        // 记录日志
        orderLogService.recordLog(id, operator, "订单退款", null);
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
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }


        // 3. 校验支付方式是否可用
        List<PayMethodConfig> methods = payMethodConfigService.getEnabledMethods(order.getMerchantId());
        boolean validMethod = methods.stream()
                .anyMatch(method -> method.getPayType().equals(dto.getPayType()));

        if (!validMethod) {
            throw new BusinessException("支付方式不可用");
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
        order.setStatus(1);
        order.setPayType(dto.getPayType());
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);

        // 6. 记录订单日志
        orderLogService.recordLog(order.getId(), "SYSTEM", "支付成功",
                String.format("支付方式：%s，支付金额：%s，流水号：%s",
                        dto.getPayType(), dto.getPayAmount(), dto.getTradeNo()));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderTimeout() {
        // 1. 查询超时未支付订单（假设30分钟超时）
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);

        List<Order> orders = this.lambdaQuery()
                .eq(Order::getStatus, 0)
                .le(Order::getCreateTime, timeout)
                .list();

        if (orders.isEmpty()) {
            return;
        }

        // 2. 批量取消订单
        for (Order order : orders) {
            try {
                // 更新订单状态
                order.setStatus(4);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}