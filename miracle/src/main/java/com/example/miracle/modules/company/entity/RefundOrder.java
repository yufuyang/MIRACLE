package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_refund_order")
@EqualsAndHashCode(callSuper = true)
public class RefundOrder extends BaseEntity {

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商户ID
     */
    private Long merchantId;

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
     * 操作人
     */
    private String operator;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 拒绝原因
     */
    private String rejectReason;
}