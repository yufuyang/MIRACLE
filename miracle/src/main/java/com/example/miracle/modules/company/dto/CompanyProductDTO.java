package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品DTO
 */
@Data
public class CompanyProductDTO {
    /**
     * ID
     */
    private Long id;

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
     * 产品图片
     */
    private String imageUrl;

    /**
     * 产品价格
     */
    private BigDecimal price;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 状态：0-下架 1-上架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 意向数
     */
    private Integer intentionCount;
} 