package com.example.miracle.modules.platform.dto.query;

import com.example.miracle.common.dto.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台用户分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformUserPageQuery extends PageQuery {

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