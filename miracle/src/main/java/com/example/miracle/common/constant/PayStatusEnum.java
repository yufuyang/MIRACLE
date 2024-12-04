package com.example.miracle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStatusEnum {

    UNPAID(0, "未支付"),
    PAYING(1, "支付中"),
    PAID(2, "支付成功"),
    FAILED(3, "支付失败"),
    CLOSED(4, "已关闭");

    private final Integer code;
    private final String desc;

    public static PayStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}