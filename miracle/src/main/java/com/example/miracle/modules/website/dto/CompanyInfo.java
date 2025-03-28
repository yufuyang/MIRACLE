package com.example.miracle.modules.website.dto;

import lombok.Data;

@Data
public class CompanyInfo {

    private Long id;
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司logo地址
     */
    private String logoUrl;

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

    /**
     * 公司描述
     */
    private String companyDesc;
    /**
     * 产品数量
     */
    private Integer productCount;
    /**
     * 意向客户数量
     */
    private Integer intentionCount;
}
