package com.example.miracle.modules.platform.dto.query;

import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantPageQuery extends PageQuery {

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 状态
     */
    private Integer status;
} 