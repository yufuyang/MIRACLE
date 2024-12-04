package com.example.miracle.modules.company.dto;
import lombok.Data;

import java.util.List;

@Data
public class CompanyDashboardDTO {

    /**
     * 商户总数
     */
    private Integer merchantCount;

    /**
     * 待审核商户数
     */
    private Integer pendingMerchantCount;

    /**
     * 本月新增商户数
     */
    private Integer newMerchantCount;

    /**
     * 本月活跃商户数
     */
    private Integer activeMerchantCount;

    /**
     * 商户排名前5
     */
    private List<MerchantRankDTO> topMerchants;

    /**
     * 商品排名前5
     */
    private List<ProductRankDTO> topProducts;
}
