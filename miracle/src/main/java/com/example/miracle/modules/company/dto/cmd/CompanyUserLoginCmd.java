package com.example.miracle.modules.company.dto.cmd;

import lombok.Data;

/**
 * 公司用户登录命令
 */
@Data
public class CompanyUserLoginCmd {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
} 