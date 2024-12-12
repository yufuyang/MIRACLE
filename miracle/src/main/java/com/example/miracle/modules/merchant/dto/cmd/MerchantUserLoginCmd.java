package com.example.miracle.modules.merchant.dto.cmd;

import lombok.Data;

/**
 * 商户用户登录命令
 */
@Data
public class MerchantUserLoginCmd {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

} 