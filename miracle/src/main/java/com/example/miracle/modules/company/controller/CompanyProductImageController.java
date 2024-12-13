package com.example.miracle.modules.company.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 公司产品图片控制器
 */
@RestController
@RequestMapping("/company/product/image")
@RequiredArgsConstructor
public class CompanyProductImageController {

    private final CompanyProductImageService imageService;
    private final BaseController baseController;
    private final CompanyProductService companyProductService;

    /**
     * 新增产品图片
     */
    @PostMapping
    public SingleResponse<CompanyProductImage> save(@RequestBody CompanyProductImage image) {
        imageService.save(image);
        return SingleResponse.of(image);
    }

    /**
     * 删除产品图片
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {

        authCheck(id);

        imageService.removeById(id);
        return SingleResponse.buildSuccess();
    }

    /**
     * 查询产品图片列表
     */
    @GetMapping("/list/{productId}")
    public MultiResponse<CompanyProductImage> listByProductId(@PathVariable Long productId) {
        return imageService.listByProductId(productId);
    }

    /**
     * 更新排序
     */
    @PutMapping("/{id}/sort")
    public SingleResponse<Boolean> updateSort(@PathVariable Long id, @RequestParam Integer sort) {

        authCheck(id);
        imageService.updateSort(id, sort);
        return SingleResponse.buildSuccess();
    }

    /**
     * 设置主图
     */
    @PutMapping("/{id}/main")
    public SingleResponse<Boolean> setMain(@PathVariable Long id) {
        authCheck(id);
        return imageService.setMain(id);
    }


    private void authCheck(Long id) {

        CompanyProductImage companyProductImage = imageService.getById(id);

        if (Objects.isNull(companyProductImage)) {
            throw new BusinessException("商品图片不存在");
        }

        CompanyProduct companyProduct = companyProductService.getById(companyProductImage.getProductId());

        if (Objects.isNull(companyProduct)) {
            throw new BusinessException("商品不存在");
        }
        Long companyId = baseController.getCompanyId();

        if (!Objects.equals(companyProduct.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }

    }
} 