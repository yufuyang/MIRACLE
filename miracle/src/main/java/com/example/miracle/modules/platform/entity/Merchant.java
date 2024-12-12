package com.example.miracle.modules.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant")
public class Merchant extends BaseEntity {

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户logo地址
     */
    private String logoUrl;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 商户描述
     */
    private String merchantDesc;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
} 