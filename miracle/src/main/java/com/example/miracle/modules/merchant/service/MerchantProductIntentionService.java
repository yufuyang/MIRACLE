package com.example.miracle.modules.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.merchant.dto.query.MerchantProductIntentionPageQuery;
import com.example.miracle.modules.merchant.entity.MerchantProductIntention;

/**
 * 商户产品意向服务接口
 */
public interface MerchantProductIntentionService extends IService<MerchantProductIntention> {

    /**
     * 分页查询商户产品意向
     */
    MultiResponse<MerchantProductIntention> pageQuery(MerchantProductIntentionPageQuery query);

} 