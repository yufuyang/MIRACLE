package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.dto.RefundAuditDTO;
import com.example.miracle.modules.company.dto.RefundDetailDTO;
import com.example.miracle.modules.company.service.RefundOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/refund")
@RequiredArgsConstructor
public class RefundOrderController {

    private final RefundOrderService refundOrderService;

    @GetMapping("/detail/{refundNo}")
    public ResultDTO<RefundDetailDTO> getDetail(@PathVariable String refundNo) {
        return ResultDTO.ok(refundOrderService.getRefundDetail(refundNo));
    }

    @PostMapping("/audit")
    public ResultDTO<Void> audit(@RequestBody RefundAuditDTO dto) {
        refundOrderService.auditRefund(dto);
        return ResultDTO.ok();
    }

    @PostMapping("/cancel/{refundNo}")
    public ResultDTO<Void> cancel(@PathVariable String refundNo, @RequestParam String operator) {
        refundOrderService.cancelRefund(refundNo, operator);
        return ResultDTO.ok();
    }
} 