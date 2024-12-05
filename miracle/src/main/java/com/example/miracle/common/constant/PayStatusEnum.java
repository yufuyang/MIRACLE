package com.example.miracle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStatusEnum {

    UNPAID(0, "未支付"),
    PAYING(1, "支付中"),
    PAID(2, "已支付"),
    REFUNDED(3, "已退款"),
    CLOSED(4, "已关闭"),
    FAILED(5, "支付失败");

    private final Integer code;
    private final String desc;

    public static boolean canRefund(Integer status) {
        return PAID.getCode().equals(status);
    }

    public static boolean canClose(Integer status) {
        return UNPAID.getCode().equals(status) || PAYING.getCode().equals(status);
    }
}