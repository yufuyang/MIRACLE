package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.CompanyMerchantCooperationDTO;
import com.example.miracle.modules.company.dto.query.CompanyMerchantCooperationPageQry;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;

public interface CompanyMerchantCooperationService extends IService<CompanyMerchantCooperation> {

    MultiResponse<CompanyMerchantCooperationDTO> page(CompanyMerchantCooperationPageQry companyMerchantCooperationPageQry);
}
