package com.example.miracle.modules.company.dto;

import lombok.Data;

/**
 * 公司用户登录返回对象
 */
@Data
public class CompanyUserLoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * token
     */
    private String token;
} 