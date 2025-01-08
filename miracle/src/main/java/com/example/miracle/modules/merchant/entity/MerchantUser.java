package com.example.miracle.modules.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_user")
public class MerchantUser extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
} 