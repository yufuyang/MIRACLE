package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.CompanyProductIntentionDTO;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.service.CompanyMerchantCooperationService;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.merchant.dto.query.MerchantProductIntentionPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantProductIntention;
import com.example.miracle.modules.merchant.service.MerchantProductIntentionService;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.platform.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/company/product/intention")
@RequiredArgsConstructor
public class CompanyProductIntentionController {

    private final BaseController baseController;

    private final MerchantProductIntentionService intentionService;

    private final MerchantService merchantService;

    private final CompanyProductService companyProductService;

    private final CompanyMerchantCooperationService companyMerchantCooperationService;


    /**
     * 分页查询意向列表
     */
    @PostMapping("/page")
    public MultiResponse<CompanyProductIntentionDTO> pageQuery(@RequestBody MerchantProductIntentionPageQuery query) {

        query.setCompanyId(baseController.getCompanyId());

        MultiResponse<MerchantProductIntention> merchantProductIntentionList = intentionService.pageQuery(query);


        List<Long> merchantIdList = merchantProductIntentionList.getData().stream().map(MerchantProductIntention::getMerchantId).collect(Collectors.toList());

        List<Long> productIdList = merchantProductIntentionList.getData().stream().map(MerchantProductIntention::getProductId).collect(Collectors.toList());

        if (merchantIdList.isEmpty() || productIdList.isEmpty()) {
            return MultiResponse.buildSuccess();
        }


        List<CompanyProductIntentionDTO> companyProductIntentionList = new ArrayList<>();

        List<Merchant> merchantList = merchantService.listByIds(merchantIdList);

        List<CompanyProduct> companyProductList = companyProductService.listByIds(productIdList);

        for (MerchantProductIntention merchantProductIntention : merchantProductIntentionList.getData()) {

            CompanyProductIntentionDTO companyProductIntentionDTO = new CompanyProductIntentionDTO();
            companyProductIntentionDTO.setId(merchantProductIntention.getId());
            companyProductIntentionDTO.setMerchantId(merchantProductIntention.getMerchantId());
            companyProductIntentionDTO.setProductId(merchantProductIntention.getProductId());
            companyProductIntentionDTO.setCreateTime(merchantProductIntention.getCreateTime());

            CompanyMerchantCooperation companyMerchantCooperation = companyMerchantCooperationService.getOne(new LambdaQueryWrapper<CompanyMerchantCooperation>()
                    .eq(CompanyMerchantCooperation::getCompanyId, baseController.getCompanyId())
                    .eq(CompanyMerchantCooperation::getMerchantId, merchantProductIntention.getMerchantId()));

            if(companyMerchantCooperation != null) {
                companyProductIntentionDTO.setStatus(1);
            }else {
                companyProductIntentionDTO.setStatus(0);
            }

            Merchant merchant = merchantList.stream().filter(item -> item.getId().equals(merchantProductIntention.getMerchantId())).findFirst().orElse(null);
            if (merchant != null) {
                companyProductIntentionDTO.setMerchantName(merchant.getMerchantName());
                companyProductIntentionDTO.setMerchantRealName(merchant.getContactName());
                companyProductIntentionDTO.setMerchantLicenseNo(merchant.getLicenseNo());
                companyProductIntentionDTO.setMerchantPhone(merchant.getContactPhone());
            }

            CompanyProduct companyProduct = companyProductList.stream().filter(item -> item.getId().equals(merchantProductIntention.getProductId())).findFirst().orElse(null);
            if (companyProduct != null) {
                companyProductIntentionDTO.setProductName(companyProduct.getProductName());
            }

            companyProductIntentionList.add(companyProductIntentionDTO);
        }

        return MultiResponse.of(companyProductIntentionList, merchantProductIntentionList.getTotal());
    }


}
