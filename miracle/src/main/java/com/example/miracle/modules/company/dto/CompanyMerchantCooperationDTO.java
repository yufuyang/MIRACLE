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
