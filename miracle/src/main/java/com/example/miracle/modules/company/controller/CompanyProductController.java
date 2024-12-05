package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.entity.Product;
import com.example.miracle.modules.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company/product")
@RequiredArgsConstructor
public class CompanyProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public ResultDTO<Page<Product>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long merchantId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return ResultDTO.ok(productService.pageProduct(current, size, merchantId,
                productName, productCode, categoryId, status));
    }

    @GetMapping("/{id}")
    public ResultDTO<Product> detail(@PathVariable Long id) {
        return ResultDTO.ok(productService.getProductDetail(id));
    }

    @PostMapping
    public ResultDTO<?> create(@RequestBody Product product,
                               @RequestParam List<String> imageUrls,
                               @RequestParam(defaultValue = "0") Integer mainImageIndex) {
        productService.createProduct(product, imageUrls, mainImageIndex);
        return ResultDTO.ok();
    }


    @PutMapping
    public ResultDTO<?> update(@RequestBody Product product,
                               @RequestParam List<String> imageUrls,
                               @RequestParam(defaultValue = "0") Integer mainImageIndex) {
        productService.updateProduct(product, imageUrls, mainImageIndex);
        return ResultDTO.ok();
    }

    @PutMapping("/status/{id}")
    public ResultDTO<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return ResultDTO.ok();
    }

    @PutMapping("/stock/{id}")
    public ResultDTO<?> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        productService.updateStock(id, stock);
        return ResultDTO.ok();
    }

    @PutMapping("/batch/status")
    public ResultDTO<?> batchUpdateStatus(
            @RequestParam List<Long> ids,
            @RequestParam Integer status) {
        productService.batchUpdateStatus(ids, status);
        return ResultDTO.ok();
    }

    /**
     * 批量删除商品
     */
    @DeleteMapping("/batch")
    public ResultDTO<?> batchDelete(@RequestParam List<Long> ids) {
        productService.batchDelete(ids);
        return ResultDTO.ok();
    }

    @PutMapping("/batch/category")
    public ResultDTO<?> batchUpdateCategory(
            @RequestParam List<Long> ids,
            @RequestParam Long categoryId) {
        productService.batchUpdateCategory(ids, categoryId);
        return ResultDTO.ok();
    }
}