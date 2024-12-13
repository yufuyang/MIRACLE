package com.example.miracle.modules.company.dto;

import lombok.Data;

import java.util.List;

/**
 * 公司产品分类树形结构DTO
 */
@Data
public class CompanyProductCategoryTreeDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 子分类列表
     */
    private List<CompanyProductCategoryTreeDTO> children;
} 