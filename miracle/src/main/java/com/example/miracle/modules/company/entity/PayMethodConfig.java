package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_pay_method_config")
@EqualsAndHashCode(callSuper = true)
public class PayMethodConfig extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 支付类型：1-微信 2-支付宝
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
     * 排序
     */
    private Integer sort;

    /**
     * 配置信息(JSON)
     */
    private String config;

    /**
     * 配置ID
     */
    private Long configId;

    /**
     * 支付配置信息
     */
    private String payConfig;

    /**
     * 备注说明
     */
    private String remark;
}