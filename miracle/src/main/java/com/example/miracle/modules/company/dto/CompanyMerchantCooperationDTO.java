package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyMerchantCooperationDTO {

    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;
    /**
     * 商户名称
     */
    private String merchantName;

    private String merchantContactName;


    private String merchantContactPhone;

    /**
     * 营业执照号
     */
    private String merchantLicenseNo;

    /**
     * 所在省份
     */
    private String merchantProvince;

    /**
     * 所在城市
     */
    private String merchantCity;

    /**
     * 详细地址
     */
    private String merchantAddress;

    /**
     * 状态 0-待处理 1-已通过 2-已拒绝 3-已解除合作
     */
    private Integer status;

    private Long companyId;

    private String companyName;

    private String companyLogo;

    private String companyContactName;


    private String companyContactPhone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
