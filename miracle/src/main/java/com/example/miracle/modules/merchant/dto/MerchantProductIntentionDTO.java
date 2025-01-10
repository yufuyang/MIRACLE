package com.example.miracle.modules.merchant.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantProductIntentionDTO {

    private Long id;

    private Long merchantId;

    private Long companyId;

    private Long productId;

    private String productName;

    private String companyName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    private String productLogo;

    private LocalDateTime createTime;
}
