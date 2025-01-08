package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyProductIntentionDTO {

    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 商户名称
     */
    private String merchantName;
    /**
     * 商户真实姓名
     */
    private String merchantRealName;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 营业执照号
     */
    private String merchantLicenseNo;
    /**
     * 商户手机号
     */
    private String merchantPhone;

    /**
     * 状态 0-未合作 1-已合作
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
