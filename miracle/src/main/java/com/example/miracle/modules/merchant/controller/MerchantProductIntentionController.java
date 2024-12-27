package com.example.miracle.modules.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.merchant.dto.query.MerchantProductIntentionPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantProductIntention;
import com.example.miracle.modules.merchant.service.MerchantProductIntentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 添加意向
     */
    @PostMapping
    public SingleResponse<Void> addIntention(@RequestBody MerchantProductIntention intention) {
        // 设置商户ID
        intention.setMerchantId(baseController.getMerchantId());
        intentionService.save(intention);

        // 更新公司产品统计
        companyProductStatsService.incrementIntentCount(intention.getCompanyId(), intention.getProductId());

        return SingleResponse.buildSuccess();
    }

    /**
     * 分页查询意向列表
     */
    @PostMapping("/page")
    public MultiResponse<MerchantProductIntention> pageQuery(@RequestBody MerchantProductIntentionPageQuery query) {
        query.setMerchantId(baseController.getMerchantId());
        return intentionService.pageQuery(query);
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