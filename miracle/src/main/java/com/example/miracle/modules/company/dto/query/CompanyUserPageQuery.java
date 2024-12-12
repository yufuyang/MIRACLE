package com.example.miracle.modules.company.dto.query;

import com.example.miracle.common.dto.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyUserPageQuery extends PageQuery {

    private Long companyId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 状态
     */
    private Integer status;
}
