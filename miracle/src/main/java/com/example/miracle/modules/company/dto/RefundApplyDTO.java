package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RefundApplyDTO {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 退款类型：1-仅退款 2-退货退款
     */
    private Integer refundType;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退货商品列表（退货退款时必填）
     */
    private List<RefundItemDTO> items;

    /**
     * 操作人
     */
    private String operator;

}
