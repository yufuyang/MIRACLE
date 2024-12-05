package com.example.miracle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核拒绝"),
    RETURNING(3, "退货中"),
    RECEIVED(4, "已收货"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    public static boolean canCancel(Integer status) {
        return PENDING.getCode().equals(status);
    }

    public static boolean canReceive(Integer status) {
        return RETURNING.getCode().equals(status);
    }
}