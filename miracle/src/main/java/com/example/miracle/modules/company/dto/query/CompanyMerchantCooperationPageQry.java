package com.example.miracle.modules.company.dto.query;

import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyMerchantCooperationPageQry extends PageQuery {

    private Long companyId;

    private Long merchantId;

    private String merchantName;

    private String companyName;

    private Integer status;
}
