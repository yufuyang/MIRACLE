package com.example.miracle.modules.platform.dto.query;

import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公司分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyPageQuery extends PageQuery {

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序字段：productCount-产品数 intentionCount-意向数
     */
    private String orderField;

    /**
     * 是否升序
     */
    private Boolean asc;
} 