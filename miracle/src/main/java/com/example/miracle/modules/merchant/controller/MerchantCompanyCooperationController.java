package com.example.miracle.modules.merchant.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;
import com.example.miracle.modules.company.service.CompanyMerchantCooperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant/company/cooperation")
@RequiredArgsConstructor
public class MerchantCompanyCooperationController {

    private final BaseController baseController;

    private final CompanyMerchantCooperationService merchantCompanyCooperationService;

    @PostMapping
    public SingleResponse handleCooperation(@RequestBody CompanyMerchantCooperation cooperation) {
        // 设置商户ID
        if (cooperation.getMerchantId() == null) {
            cooperation.setMerchantId(baseController.getMerchantId());
        }else if (!cooperation.getMerchantId().equals(baseController.getMerchantId())){
            return SingleResponse.buildFailure("无权操作");
        }
        merchantCompanyCooperationService.updateById(cooperation);

        return SingleResponse.buildSuccess();
    }
}
