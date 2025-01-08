package com.example.miracle.modules.website.dto.cmd;

import lombok.Data;

@Data
public class CompanyRegisterCmd {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

}
