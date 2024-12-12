package com.example.miracle.modules.platform.dto.cmd;

import lombok.Data;

@Data
public class PlatformUserLoginCmd {
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}
