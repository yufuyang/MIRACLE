package com.example.miracle.modules.company.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.miracle.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("product_category")
@EqualsAndHashCode(callSuper = true)
public class ProductCategory extends BaseEntity {

    /**
     * 商户ID
     */
    private Long companyId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 分类路径
     */
    private String path;

    /**
     * 商品数量
     */
    private Integer productCount;
}