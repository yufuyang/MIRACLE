package com.example.miracle.modules.merchant.dto.query;

import com.example.miracle.common.dto.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户产品意向分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantProductIntentionPageQuery extends PageQuery {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户用户ID
     */
    private Long merchantUserId;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 产品ID
     */
    private Long productId;

} 