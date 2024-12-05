package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundDetailDTO {

    /**
     * 退款单号
     */
    private String refundNo;

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
     * 状态：0-待审核 1-审核通过 2-退款中 3-已退款 4-已拒绝 5-已取消
     */
    private Integer status;

    /**
     * 审核人
     */
    private String auditUser;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退货详情（退货退款时有值）
     */
    private ReturnDetailDTO returnDetail;
}
