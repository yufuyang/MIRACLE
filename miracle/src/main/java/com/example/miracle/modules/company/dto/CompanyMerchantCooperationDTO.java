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

    /**
     * 状态 0-待处理 1-已通过 2-已拒绝 3-已解除合作
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
