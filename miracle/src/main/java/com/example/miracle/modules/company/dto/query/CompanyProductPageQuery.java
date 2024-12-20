package com.example.miracle.modules.company.dto.query;


import com.example.miracle.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 公司产品分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyProductPageQuery extends PageQuery {

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编号
     */
    private String productCode;

    /**
     * 产品分类ID
     */
    private Long categoryId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最小价格
     */
    private BigDecimal minPrice;

    /**
     * 最大价格
     */
    private BigDecimal maxPrice;

    /**
     * 排序字段：price-价格 viewCount-浏览量 intentionCount-意向数
     */
    private String orderField;

    /**
     * 是否升序
     */
    private Boolean asc;

    /**
     * 是否是商户查询
     */
    private boolean merchantQuery;
} 