package com.example.miracle.modules.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.merchant.dto.query.MerchantProductIntentionPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantProductIntention;
import com.example.miracle.modules.merchant.mapper.MerchantProductIntentionMapper;
import com.example.miracle.modules.merchant.service.MerchantProductIntentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 商户产品意向服务实现类
 */
@Service
@RequiredArgsConstructor
public class MerchantProductIntentionServiceImpl extends ServiceImpl<MerchantProductIntentionMapper, MerchantProductIntention> implements MerchantProductIntentionService {


    @Override
    public MultiResponse<MerchantProductIntention> pageQuery(MerchantProductIntentionPageQuery query) {
        LambdaQueryWrapper<MerchantProductIntention> wrapper = new LambdaQueryWrapper<MerchantProductIntention>()
                .eq(query.getMerchantId() != null, MerchantProductIntention::getMerchantId, query.getMerchantId())
                .eq(query.getCompanyId() != null, MerchantProductIntention::getCompanyId, query.getCompanyId())
                .eq(query.getProductId() != null, MerchantProductIntention::getProductId, query.getProductId())
                .orderByDesc(MerchantProductIntention::getCreateTime);

        Page<MerchantProductIntention> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }

} 