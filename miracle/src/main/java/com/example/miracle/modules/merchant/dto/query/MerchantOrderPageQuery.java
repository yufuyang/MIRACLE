package com.example.miracle.modules.merchant.dto.query;

import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantOrderPageQuery extends PageQuery {

    private Long merchantId;

    private Long companyId;

    private Long productId;

    private Integer status;

    private String receiverName;

    private String receiverPhone;


}
