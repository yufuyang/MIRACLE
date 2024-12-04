package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.company.entity.Product;
import com.example.miracle.modules.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public Result<Page<Product>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long merchantId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return Result.ok(productService.pageProduct(current, size, merchantId,
                productName, productCode, categoryId, status));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getProductDetail(id));
    }

    @PostMapping
    public Result<?> create( @RequestBody Product product) {
        productService.createProduct(product);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.ok();
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/stock/{id}")
    public Result<?> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        productService.updateStock(id, stock);
        return Result.ok();
    }

    @PutMapping("/batch/status")
    public Result<?> batchUpdateStatus(
            @RequestParam List<Long> ids,
            @RequestParam Integer status) {
        productService.batchUpdateStatus(ids, status);
        return Result.ok();
    }

    /**
     * 批量删除商品
     */
    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestParam List<Long> ids) {
        productService.batchDelete(ids);
        return Result.ok();
    }

    @PutMapping("/batch/category")
    public Result<?> batchUpdateCategory(
            @RequestParam List<Long> ids,
            @RequestParam Long categoryId) {
        productService.batchUpdateCategory(ids, categoryId);
        return Result.ok();
    }
}