package com.example.miracle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RefundStatusEnum {

    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核拒绝"),
    PROCESSING(3, "处理中"),
    COMPLETED(4, "已完成"),
    FAILED(5, "已失败"),
    CANCELLED(6, "已取消");

    private final Integer code;
    private final String desc;

    public static boolean canProcess(Integer status) {
        return APPROVED.getCode().equals(status);
    }

    public static boolean canCancel(Integer status) {
        return PENDING.getCode().equals(status);
    }

    public static boolean canAudit(Integer status) {
        return PENDING.getCode().equals(status);
    }
}
