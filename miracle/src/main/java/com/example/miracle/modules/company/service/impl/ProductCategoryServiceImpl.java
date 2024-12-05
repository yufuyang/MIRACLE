package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.Product;
import com.example.miracle.modules.company.entity.ProductCategory;
import com.example.miracle.modules.company.mapper.ProductCategoryMapper;
import com.example.miracle.modules.company.service.ProductCategoryService;
import com.example.miracle.modules.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    @Override
    public Page<ProductCategory> pageCategory(Integer current, Integer size, Long companyId, String categoryName) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getCompanyId, companyId)
                .like(StringUtils.hasText(categoryName), ProductCategory::getCategoryName, categoryName)
                .orderByDesc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreateTime);

        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "category", allEntries = true)
    public void createCategory(ProductCategory category) {
        // 检查分类名称是否重复
        if (this.lambdaQuery()
                .eq(ProductCategory::getCompanyId, category.getCompanyId())
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
        // 设置层级关系
        if (category.getParentId() != null) {
            ProductCategory parent = this.getById(category.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
            category.setPath(parent.getPath() + "," + parent.getId());
        } else {
            category.setLevel(1);
            category.setPath("0");
        }

        this.save(category);
        // 更新分类商品数量
        updateCategoryProductCount(category.getId());
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
                .eq(ProductCategory::getCompanyId, category.getCompanyId())
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
    @Cacheable(value = "category", key = "'company:' + #companyId")
    public List<ProductCategory> listEnabledCategories(Long companyId) {
        return this.lambdaQuery()
                .eq(ProductCategory::getCompanyId, companyId)
                .eq(ProductCategory::getStatus, 1)
                .orderByDesc(ProductCategory::getSort)
                .list();
    }

    /**
     * 更新分类商品数量
     */
    private void updateCategoryProductCount(Long categoryId) {
        Long count = productService.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, 1));
        
        this.lambdaUpdate()
                .set(ProductCategory::getProductCount, count)
                .eq(ProductCategory::getId, categoryId)
                .update();

        // 更新父分类商品数量
        ProductCategory category = this.getById(categoryId);
        if (category.getParentId() != null) {
            updateCategoryProductCount(category.getParentId());
        }
    }
}