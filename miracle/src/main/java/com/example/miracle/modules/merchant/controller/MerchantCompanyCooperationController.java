package com.example.miracle.modules.merchant.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.CompanyMerchantCooperationDTO;
import com.example.miracle.modules.company.dto.query.CompanyMerchantCooperationPageQry;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;
import com.example.miracle.modules.company.service.CompanyMerchantCooperationService;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
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

    private final CompanyService companyService;

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


    @PostMapping("/page")
    public MultiResponse<CompanyMerchantCooperationDTO> page(@RequestBody CompanyMerchantCooperationPageQry companyMerchantCooperationPageQry){

        Long merchantId = baseController.getMerchantId();

        companyMerchantCooperationPageQry.setMerchantId(merchantId);

        MultiResponse<CompanyMerchantCooperationDTO> merchantCooperationDTOMultiResponse = merchantCompanyCooperationService.page(companyMerchantCooperationPageQry);

        if (CollectionUtils.isEmpty(merchantCooperationDTOMultiResponse.getData())) {
            return merchantCooperationDTOMultiResponse;
        }

        merchantCooperationDTOMultiResponse.getData().forEach(companyMerchantCooperationDTO -> {

            Company company = companyService.getById(companyMerchantCooperationDTO.getCompanyId());
            if (company == null) {
                return;
            }
            companyMerchantCooperationDTO.setCompanyName(company.getCompanyName());
            companyMerchantCooperationDTO.setCompanyLogo(company.getLogoUrl());
            companyMerchantCooperationDTO.setContactName(company.getContactName());
            companyMerchantCooperationDTO.setContactPhone(company.getContactPhone());

        });


        return merchantCooperationDTOMultiResponse;
    }
}
