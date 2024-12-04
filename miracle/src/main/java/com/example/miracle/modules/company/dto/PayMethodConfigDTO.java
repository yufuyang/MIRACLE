package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PayMethodConfigDTO {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 支付方式：1-微信 2-支付宝 3-银行卡 4-现金
     */
    private Integer payType;

    /**
     * 支付方式名称
     */
    private String methodName;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 支付配置信息
     */
    private Map<String, Object> config;
}