package com.example.miracle.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("company_user")
@EqualsAndHashCode(callSuper = true)
public class CompanyUser extends BaseEntity {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;
}
