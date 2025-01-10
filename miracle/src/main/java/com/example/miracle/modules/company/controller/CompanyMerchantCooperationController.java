package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.CompanyMerchantCooperationDTO;
import com.example.miracle.modules.company.dto.query.CompanyMerchantCooperationPageQry;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.service.CompanyMerchantCooperationService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/company/merchant/cooperation")
@RequiredArgsConstructor
public class CompanyMerchantCooperationController {

    private final CompanyMerchantCooperationService companyMerchantCooperationService;

    private final BaseController baseController;

    @PostMapping("/page")
    public MultiResponse<CompanyMerchantCooperationDTO> page(@RequestBody CompanyMerchantCooperationPageQry companyMerchantCooperationPageQry){

        Long companyId = baseController.getCompanyId();

        companyMerchantCooperationPageQry.setCompanyId(companyId);

        return companyMerchantCooperationService.page(companyMerchantCooperationPageQry);
    }

    @DeleteMapping("/dissolution/{id}")
    public SingleResponse dissolution(@PathVariable Long id) {

        CompanyMerchantCooperation companyMerchantCooperation = companyMerchantCooperationService.getById(id);

        if (Objects.isNull(companyMerchantCooperation)) {
            throw new BusinessException("合作关系不存在");
        }

        Long companyId = baseController.getCompanyId();

        if (!Objects.equals(companyMerchantCooperation.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }


        companyMerchantCooperation.setStatus(3);
        companyMerchantCooperationService.updateById(companyMerchantCooperation);

        return SingleResponse.buildSuccess();
    }

    @PostMapping
    public SingleResponse save(@RequestBody CompanyMerchantCooperation companyMerchantCooperation) {

        Long companyId = baseController.getCompanyId();

        CompanyMerchantCooperation merchantCooperation = companyMerchantCooperationService.getOne(new LambdaQueryWrapper<CompanyMerchantCooperation>()
                .eq(CompanyMerchantCooperation::getMerchantId, companyMerchantCooperation.getMerchantId())
                .eq(CompanyMerchantCooperation::getCompanyId, companyId)
        );

        if (Objects.nonNull(merchantCooperation) ) {

            if (merchantCooperation.getStatus() == 0){
                throw new BusinessException("已存在待处理关系");
            }

            if (merchantCooperation.getStatus() == 1){
                throw new BusinessException("已存在合作关系");
            }

            if (merchantCooperation.getStatus() == 3){
                merchantCooperation.setStatus(0);
                companyMerchantCooperationService.updateById(merchantCooperation);
                return SingleResponse.buildSuccess();
            }
        }

        companyMerchantCooperation.setCompanyId(companyId);
        companyMerchantCooperation.setStatus(0);

        companyMerchantCooperationService.save(companyMerchantCooperation);

        return SingleResponse.buildSuccess();
    }


}
