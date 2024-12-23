package com.example.miracle.modules.website.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.CompanyProductCategoryTreeDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductCategoryQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductCategory;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.service.CompanyProductCategoryService;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 公司产品分类控制器
 */
@RestController
@RequestMapping("/website/product/category")
@RequiredArgsConstructor
public class WebsiteCompanyProductCategoryController {

    private final CompanyProductCategoryService categoryService;

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public SingleResponse<CompanyProductCategory> getById(@PathVariable Long id) {

        return SingleResponse.of(categoryService.getById(id));
    }

    /**
     * 查询分类列表
     */
    @PostMapping("/list")
    public MultiResponse<CompanyProductCategory> list(@RequestBody CompanyProductCategoryQuery query) {


        return categoryService.list(query);
    }



    /**
     * 获取分类树形结构
     */
    @GetMapping("/tree/{companyId}")
    public MultiResponse<CompanyProductCategoryTreeDTO> tree(@PathVariable Long companyId) {
        return categoryService.tree(companyId);
    }



} 