package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_merchant_cooperation")
public class CompanyMerchantCooperation extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 状态 0-待处理 1-已通过 2-已拒绝 3-已解除合作
     */
    private Integer status;
}
