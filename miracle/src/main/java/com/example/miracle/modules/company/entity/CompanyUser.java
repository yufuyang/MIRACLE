package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公司用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_user")
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
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    @TableField(exist = false)
    private String role;
} 