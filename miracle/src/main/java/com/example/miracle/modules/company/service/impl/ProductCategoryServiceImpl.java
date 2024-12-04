package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.ProductCategory;
import com.example.miracle.modules.company.mapper.ProductCategoryMapper;
import com.example.miracle.modules.company.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Override
    public Page<ProductCategory> pageCategory(Integer current, Integer size, Long merchantId, String categoryName) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getMerchantId, merchantId)
                .like(StringUtils.hasText(categoryName), ProductCategory::getCategoryName, categoryName)
                .orderByDesc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreateTime);

        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(ProductCategory category) {
        // 检查分类名称是否重复
        if (this.lambdaQuery()
                .eq(ProductCategory::getMerchantId, category.getMerchantId())
                .eq(ProductCategory::getCategoryName, category.getCategoryName())
                .exists()) {
            throw new BusinessException("分类名称已存在");
        }

        // 设置默认值
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }

        this.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(ProductCategory category) {
        // 检查分类是否存在
        ProductCategory existCategory = this.getById(category.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类名称是否重复
        if (this.lambdaQuery()
                .eq(ProductCategory::getMerchantId, category.getMerchantId())
                .eq(ProductCategory::getCategoryName, category.getCategoryName())
                .ne(ProductCategory::getId, category.getId())
                .exists()) {
            throw new BusinessException("分类名称已存在");
        }

        this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        ProductCategory category = this.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        category.setStatus(status);
        this.updateById(category);
    }

    @Override
    public List<ProductCategory> listEnabledCategories(Long merchantId) {
        return this.lambdaQuery()
                .eq(ProductCategory::getMerchantId, merchantId)
                .eq(ProductCategory::getStatus, 1)
                .orderByDesc(ProductCategory::getSort)
                .list();
    }
}