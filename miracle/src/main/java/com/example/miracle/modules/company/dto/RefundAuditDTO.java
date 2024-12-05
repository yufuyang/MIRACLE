package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class RefundAuditDTO {

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 审核状态：1-通过 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 退货地址（退货退款且审核通过时必填）
     */
    private String returnAddress;

    /**
     * 操作人
     */
    private String operator;
}
