package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.company.entity.ProductCategory;
import com.example.miracle.modules.company.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company/product/category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    /**
     * 分页查询商品分类
     */
    @GetMapping("/page")
    public Result<Page<ProductCategory>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long merchantId,
            @RequestParam(required = false) String categoryName) {
        return Result.ok(categoryService.pageCategory(current, size, merchantId, categoryName));
    }

    /**
     * 获取商户的所有启用分类
     */
    @GetMapping("/list")
    public Result<List<ProductCategory>> list(@RequestParam Long merchantId) {
        return Result.ok(categoryService.listEnabledCategories(merchantId));
    }

    /**
     * 创建商品分类
     */
    @PostMapping
    public Result<?> create(@Validated @RequestBody ProductCategory category) {
        categoryService.createCategory(category);
        return Result.ok();
    }

    /**
     * 更新商品分类
     */
    @PutMapping
    public Result<?> update(@Validated @RequestBody ProductCategory category) {
        categoryService.updateCategory(category);
        return Result.ok();
    }

    /**
     * 更新分类状态
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        categoryService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 删除商品分类
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.ok();
    }
}
