package com.example.miracle.modules.platform.entity;

import com.example.miracle.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("platform_user")
public class PlatformUser extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private String phone;

    private Integer status;

    private LocalDateTime lastLoginTime;
}