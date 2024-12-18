package com.example.miracle.modules.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户产品意向
 */
@Data
@TableName("merchant_product_intention")
public class MerchantProductIntention extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 产品ID
     */
    private Long productId;

} 