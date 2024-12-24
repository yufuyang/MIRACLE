package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 公司产品图片控制器
 */
@RestController
@RequestMapping("/company/product/image")
@RequiredArgsConstructor
public class CompanyProductImageController {

    private final BaseController baseController;
    private final CompanyProductService companyProductService;
    private final CompanyProductImageService productImageService;

    /**
     * 获取产品图片列表
     */
    @GetMapping("/{productId}")
    public MultiResponse<CompanyProductImage> list(@PathVariable Long productId) {
        // 校验权限
        authCheck(productId);

        List<CompanyProductImage> images = productImageService.list(
            new LambdaQueryWrapper<CompanyProductImage>()
                .eq(CompanyProductImage::getProductId, productId)
                .orderByAsc(CompanyProductImage::getSort)
        );

        return MultiResponse.of(images);
    }

    /**
     * 添加产品图片
     */
    @PostMapping
    public SingleResponse<CompanyProductImage> save(@RequestBody CompanyProductImage productImage) {
        // 校验权限
        authCheck(productImage.getProductId());

        // 如果是主图，先将其他图片设置为非主图
        if (productImage.getIsMain() == 1) {
            setOtherImagesNotMain(productImage.getProductId());
        }

        productImageService.save(productImage);
        return SingleResponse.of(productImage);
    }

    /**
     * 删除产品图片
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {
        CompanyProductImage image = productImageService.getById(id);
        if (Objects.isNull(image)) {
            throw new BusinessException("图片不存在");
        }

        // 校验权限
        authCheck(image.getProductId());

        productImageService.removeById(id);
        return SingleResponse.buildSuccess();
    }

    /**
     * 设置主图
     */
    @PutMapping("/{id}/main")
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse<Boolean> setMainImage(@PathVariable Long id) {
        CompanyProductImage image = productImageService.getById(id);
        if (Objects.isNull(image)) {
            throw new BusinessException("图片不存在");
        }

        // 校验权限
        authCheck(image.getProductId());

        // 将其他图片设置为非主图
        setOtherImagesNotMain(image.getProductId());

        // 设置当前图片为主图
        image.setIsMain(1);
        productImageService.updateById(image);

        // 更新产品主图
        CompanyProduct product = companyProductService.getById(image.getProductId());
        product.setImageUrl(image.getImageUrl());
        companyProductService.updateById(product);

        return SingleResponse.buildSuccess();
    }

    /**
     * 更新图片排序
     */
    @PutMapping("/sort")
    public SingleResponse<Boolean> updateSort(@RequestBody List<CompanyProductImage> images) {
        if (images.isEmpty()) {
            return SingleResponse.buildSuccess();
        }

        // 校验权限
        authCheck(images.get(0).getProductId());

        for (CompanyProductImage image : images) {
            if (Objects.isNull(image.getId())) {
                throw new BusinessException("图片ID不能为空");
            }
            productImageService.updateById(image);
        }

        return SingleResponse.buildSuccess();
    }

    private void setOtherImagesNotMain(Long productId) {
        productImageService.update(
            new LambdaUpdateWrapper<CompanyProductImage>()
                .eq(CompanyProductImage::getProductId, productId)
                .set(CompanyProductImage::getIsMain, 0)
        );
    }

    private void authCheck(Long productId) {
        CompanyProduct product = companyProductService.getById(productId);
        if (Objects.isNull(product)) {
            throw new BusinessException("产品不存在");
        }

        Long companyId = baseController.getCompanyId();
        if (!Objects.equals(product.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }
    }
} 