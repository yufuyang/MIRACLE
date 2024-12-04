package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 分页查询商品分类
     */
    Page<ProductCategory> pageCategory(Integer current, Integer size, Long merchantId, String categoryName);

    /**
     * 创建商品分类
     */
    void createCategory(ProductCategory category);

    /**
     * 更新商品分类
     */
    void updateCategory(ProductCategory category);

    /**
     * 更新分类状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取商户的所有启用分类
     */
    List<ProductCategory> listEnabledCategories(Long merchantId);
}