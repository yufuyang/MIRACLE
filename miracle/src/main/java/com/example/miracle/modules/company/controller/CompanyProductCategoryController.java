package com.example.miracle.modules.company.controller;

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
@RequestMapping("/company/product/category")
@RequiredArgsConstructor
public class CompanyProductCategoryController {
    private final BaseController baseController;
    private final CompanyProductCategoryService categoryService;
    private final CompanyProductService productService;
    private final CompanyProductImageService productImageService;

    /**
     * 新增分类
     */
    @PostMapping
    public SingleResponse<CompanyProductCategory> save(@RequestBody CompanyProductCategory category) {

        Long companyId = baseController.getCompanyId();
        category.setCompanyId(companyId);

        categoryService.save(category);
        return SingleResponse.of(category);
    }

    /**
     * 修改分类
     */
    @PutMapping
    public SingleResponse<CompanyProductCategory> update(@RequestBody CompanyProductCategory category) {

        authCheck(category.getId());

        categoryService.updateById(category);
        return SingleResponse.of(category);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public SingleResponse<CompanyProductCategory> getById(@PathVariable Long id) {

        return SingleResponse.of(categoryService.getById(id));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {
        // 权限校验
        authCheck(id);

        // 检查是否有子分类
        long childCount = categoryService.count(new LambdaQueryWrapper<CompanyProductCategory>()
                .eq(CompanyProductCategory::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }

        // 查询分类下的所有产品
        List<CompanyProduct> products = productService.list(new LambdaQueryWrapper<CompanyProduct>().eq(CompanyProduct::getCategoryId, id));

        // 删除产品及其关联的图片
        for (CompanyProduct product : products) {
            // 删除产品图片
            productImageService.remove(new LambdaQueryWrapper<CompanyProductImage>().eq(CompanyProductImage::getProductId, product.getId()));
            // 删除产品
            productService.removeById(product.getId());
        }

        // 删除分类
        categoryService.removeById(id);

        return SingleResponse.buildSuccess();
    }

    /**
     * 查询分类列表
     */
    @PostMapping("/list")
    public MultiResponse<CompanyProductCategory> list(@RequestBody CompanyProductCategoryQuery query) {

        Long companyId = baseController.getCompanyId();
        query.setCompanyId(companyId);

        return categoryService.list(query);
    }

    /**
     * 更新排序
     */
    @PutMapping("/{id}/sort")
    public SingleResponse<Boolean> updateSort(@PathVariable Long id, @RequestParam Integer sort) {

        authCheck(id);

        return categoryService.updateSort(id, sort);
    }

    /**
     * 获取分类树形结构
     */
    @GetMapping("/tree")
    public MultiResponse<CompanyProductCategoryTreeDTO> tree() {

        Long companyId = baseController.getCompanyId();

        return categoryService.tree(companyId);
    }


    private void authCheck(Long id) {

        CompanyProductCategory companyProductCategory = categoryService.getById(id);

        if (Objects.isNull(companyProductCategory)) {
            throw new BusinessException("分类不存在");
        }

        Long companyId = baseController.getCompanyId();

        if (!Objects.equals(companyProductCategory.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }

    }
} 