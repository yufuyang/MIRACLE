package com.example.miracle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RefundTypeEnum {

    ONLY_REFUND(1, "仅退款"),
    RETURN_REFUND(2, "退货退款");

    private final Integer code;
    private final String desc;
}