package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("merchant")
@EqualsAndHashCode(callSuper = true)
public class Merchant extends BaseEntity {

    /**
     * 所属公司ID
     */
    private Long companyId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 商户地址
     */
    private String address;

    /**
     * 营业执照号
     */
    private String businessLicenseNo;

    /**
     * 营业执照图片
     */
    private String businessLicenseImage;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 状态：0-待审核 1-正常 2-禁用
     */
    private Integer status;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    private Long auditUserId;
}