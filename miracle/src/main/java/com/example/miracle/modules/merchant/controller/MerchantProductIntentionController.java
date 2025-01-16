package com.example.miracle.modules.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyMerchantCooperation;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.service.CompanyMerchantCooperationService;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.merchant.dto.MerchantProductIntentionDTO;
import com.example.miracle.modules.merchant.dto.query.MerchantProductIntentionPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantProductIntention;
import com.example.miracle.modules.merchant.service.MerchantProductIntentionService;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商户产品意向控制器
 */
@RestController
@RequestMapping("/merchant/product/intention")
@RequiredArgsConstructor
public class MerchantProductIntentionController {

    private final BaseController baseController;
    private final MerchantProductIntentionService intentionService;
    private final CompanyProductStatsService companyProductStatsService;

    private final CompanyService companyService;

    private final CompanyProductService companyProductService;
    private final CompanyMerchantCooperationService companyMerchantCooperationService;

    /**
     * 添加意向
     */
    @PostMapping
    public SingleResponse<Void> addIntention(@RequestBody MerchantProductIntention intention) {

        CompanyProduct companyProduct = companyProductService.getById(intention.getProductId());
        if (companyProduct == null) {
            return SingleResponse.buildFailure("产品不存在");
        }

        MerchantProductIntention merchantProductIntention = intentionService.getOne(new LambdaQueryWrapper<MerchantProductIntention>()
                .eq(MerchantProductIntention::getMerchantId, baseController.getMerchantId())
                .eq(MerchantProductIntention::getProductId, intention.getProductId()));

        if (Objects.nonNull(merchantProductIntention)) {
            return SingleResponse.buildFailure("已存在意向");
        }

        // 设置商户ID
        intention.setMerchantId(baseController.getMerchantId());
        intention.setCompanyId(companyProduct.getCompanyId());
        intentionService.save(intention);

        // 更新公司产品统计
        companyProductStatsService.incrementIntentCount(intention.getCompanyId(), intention.getProductId());

        return SingleResponse.buildSuccess();
    }

    /**
     * 分页查询意向列表
     */
    @PostMapping("/page")
    public MultiResponse<MerchantProductIntentionDTO> pageQuery(@RequestBody MerchantProductIntentionPageQuery query) {

        query.setMerchantId(baseController.getMerchantId());

        MultiResponse<MerchantProductIntention> merchantProductIntentionMultiResponse = intentionService.pageQuery(query);

        if (CollectionUtils.isEmpty(merchantProductIntentionMultiResponse.getData())) {
            return MultiResponse.buildSuccess();
        }

        List<Long> companyIdList = merchantProductIntentionMultiResponse.getData().stream().map(MerchantProductIntention::getCompanyId).distinct().collect(Collectors.toList());

        List<Long> productIdList = merchantProductIntentionMultiResponse.getData().stream().map(MerchantProductIntention::getProductId).collect(Collectors.toList());


        List<Company> companyList = companyService.listByIds(companyIdList);

        List<CompanyProduct> companyProducts = companyProductService.listByIds(productIdList);

        List<MerchantProductIntentionDTO> merchantProductIntentionList = new ArrayList<>();

        for (MerchantProductIntention merchantProductIntention : merchantProductIntentionMultiResponse.getData()) {

            MerchantProductIntentionDTO merchantProductIntentionDTO = new MerchantProductIntentionDTO();
            merchantProductIntentionDTO.setId(merchantProductIntention.getId());
            merchantProductIntentionDTO.setMerchantId(merchantProductIntention.getMerchantId());
            merchantProductIntentionDTO.setProductId(merchantProductIntention.getProductId());
            merchantProductIntentionDTO.setCreateTime(merchantProductIntention.getCreateTime());

            companyList.stream().filter(company -> company.getId().equals(merchantProductIntention.getCompanyId())).findFirst().ifPresent(company -> {
                merchantProductIntentionDTO.setCompanyName(company.getCompanyName());
                merchantProductIntentionDTO.setContactName(company.getContactName());
                merchantProductIntentionDTO.setContactPhone(company.getContactPhone());
            });

            companyProducts.stream().filter(companyProduct -> companyProduct.getId().equals(merchantProductIntention.getProductId())).findFirst().ifPresent(companyProduct -> {
                merchantProductIntentionDTO.setProductName(companyProduct.getProductName());
                merchantProductIntentionDTO.setProductLogo(companyProduct.getImageUrl());
            });

            merchantProductIntentionList.add(merchantProductIntentionDTO);
        }

        return MultiResponse.of(merchantProductIntentionList, merchantProductIntentionMultiResponse.getTotal());
    }

    /**
     * 取消意向
     */
    @PostMapping("/cancel")
    public SingleResponse<Void> cancelIntention(@RequestBody MerchantProductIntention intention) {

        intentionService.remove(new LambdaUpdateWrapper<MerchantProductIntention>()
                .eq(MerchantProductIntention::getMerchantId, baseController.getMerchantId())
                .eq(MerchantProductIntention::getProductId, intention.getProductId()));

//        // 更新公司产品统计
        companyProductStatsService.decrementIntentCount(intention.getProductId());

        return SingleResponse.buildSuccess();
    }
} 