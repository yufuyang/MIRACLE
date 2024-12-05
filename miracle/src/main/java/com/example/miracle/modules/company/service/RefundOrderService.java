package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.RefundApplyDTO;
import com.example.miracle.modules.company.dto.RefundAuditDTO;
import com.example.miracle.modules.company.dto.RefundDetailDTO;
import com.example.miracle.modules.company.entity.RefundOrder;

public interface RefundOrderService extends IService<RefundOrder> {

    /**
     * 申请退款
     */
    RefundOrder applyRefund(RefundApplyDTO dto);

    /**
     * 审核退款
     */
    void auditRefund(RefundAuditDTO dto);

    /**
     * 处理退款
     */
    void processRefund(String refundNo);

    /**
     * 取消退款
     */
    void cancelRefund(String refundNo, String operator);

    /**
     * 查询退款单详情
     */
    RefundDetailDTO getRefundDetail(String refundNo);
}