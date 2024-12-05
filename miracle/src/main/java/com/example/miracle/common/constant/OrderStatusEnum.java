package com.example.miracle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    UNPAID(0, "待支付"),
    PAID(1, "已支付"),
    DELIVERED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    REFUNDED(5, "已退款"),
    CLOSED(6, "已关闭");

    private final Integer code;
    private final String desc;

    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断状态是否可以转换
     */
    public static boolean canTransfer(Integer from, Integer to) {
        // 待支付 -> 已支付、已取消、已关闭
        if (UNPAID.code.equals(from)) {
            return PAID.code.equals(to) || CANCELLED.code.equals(to) || CLOSED.code.equals(to);
        }
        // 已支付 -> 已发货、已退款
        if (PAID.code.equals(from)) {
            return DELIVERED.code.equals(to) || REFUNDED.code.equals(to);
        }
        // 已发货 -> 已完成、已退款
        if (DELIVERED.code.equals(from)) {
            return COMPLETED.code.equals(to) || REFUNDED.code.equals(to);
        }
        // 其他状态不允许变更
        return false;
    }

    /**
     * 是否是终态
     */
    public static boolean isFinalStatus(Integer status) {
        return COMPLETED.code.equals(status) 
            || CANCELLED.code.equals(status) 
            || REFUNDED.code.equals(status)
            || CLOSED.code.equals(status);
    }

    /**
     * 是否可以退款
     */
    public static boolean canRefund(Integer status) {
        return PAID.code.equals(status) || DELIVERED.code.equals(status);
    }

    /**
     * 是否可以发货
     */
    public static boolean canDeliver(Integer status) {
        return PAID.code.equals(status);
    }

    /**
     * 是否可以确认收货
     */
    public static boolean canConfirm(Integer status) {
        return DELIVERED.code.equals(status);
    }
}