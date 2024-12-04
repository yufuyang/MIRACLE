package com.example.miracle.modules.company.dto;

import lombok.Data;

@Data
public class CompanyLoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}