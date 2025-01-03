package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.CompanyProductDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 公司产品控制器
 */
@RestController
@RequestMapping("/company/product")
@RequiredArgsConstructor
public class CompanyProductController {

    private final BaseController baseController;
    private final CompanyProductService companyProductService;
    private final CompanyProductImageService productImageService;
    private final CompanyProductStatsService companyProductStatsService;

    /**
     * 新增产品
     */
    @PostMapping
    public SingleResponse<CompanyProduct> save(@RequestBody CompanyProduct companyProduct) {
        Long companyId = baseController.getCompanyId();
        companyProduct.setCompanyId(companyId);

        companyProductService.saveProduct(companyProduct);

//        companyProductStatsService.incrementViewCount(companyProduct.getCompanyId(),companyProduct.getId());

        return SingleResponse.buildSuccess();
    }

    /**
     * 修改产品
     */
    @PutMapping
    public SingleResponse<CompanyProduct> update(@RequestBody CompanyProduct companyProduct) {
        // 校验权限
        authCheck(companyProduct.getId());

        return SingleResponse.of(companyProductService.updateProduct(companyProduct));
    }

    /**
     * 获取产品详情
     */
    @GetMapping("/{id}")
    public SingleResponse<CompanyProduct> getById(@PathVariable Long id) {

        return SingleResponse.of(companyProductService.getById(id));
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {

        authCheck(id);

        // 删除产品图片
        productImageService.remove(new LambdaQueryWrapper<CompanyProductImage>().eq(CompanyProductImage::getProductId, id));

        // 删除产品
        companyProductService.removeById(id);

        companyProductStatsService.remove(new LambdaQueryWrapper<CompanyProductStats>().eq(CompanyProductStats::getProductId, id));
        
        return SingleResponse.buildSuccess();
    }

    /**
     * 分页查询产品列表
     */
    @PostMapping("/page")
    public MultiResponse<CompanyProduct> pageQuery(@RequestBody CompanyProductPageQuery companyProductPageQuery) {
        Long companyId = baseController.getCompanyId();
        companyProductPageQuery.setCompanyId(companyId);

        return companyProductService.pageQuery(companyProductPageQuery);
    }


    private void authCheck(Long id) {

        CompanyProduct companyProduct = companyProductService.getById(id);

        if (Objects.isNull(companyProduct)) {
            throw new BusinessException("商品不存在");
        }

        Long companyId = baseController.getCompanyId();

        if (!Objects.equals(companyProduct.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }

    }
} 