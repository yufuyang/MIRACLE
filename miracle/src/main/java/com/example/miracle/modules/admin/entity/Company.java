package com.example.miracle.modules.admin.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
@Data
@TableName("company")
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity {

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司编码
     */
    private String companyCode;

    /**
     * 法人代表
     */
    private String legalPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 营业执照图片
     */
    private String licenseImage;

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