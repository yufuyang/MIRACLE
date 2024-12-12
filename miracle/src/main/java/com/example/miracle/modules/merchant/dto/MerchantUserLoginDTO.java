package com.example.miracle.modules.merchant.dto;

import lombok.Data;

/**
 * 商户用户登录返回对象
 */
@Data
public class MerchantUserLoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * token
     */
    private String token;
} 