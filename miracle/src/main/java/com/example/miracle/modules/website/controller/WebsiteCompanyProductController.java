package com.example.miracle.modules.website.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.entity.CompanyProductStep;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.company.service.CompanyProductStepService;
import com.example.miracle.modules.website.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 公司产品控制器
 */
@RestController
@RequestMapping("/website/product")
@RequiredArgsConstructor
public class WebsiteCompanyProductController {


    private final CompanyProductService companyProductService;

    private final CompanyProductStatsService companyProductStatsService;

    private final CompanyProductStepService companyProductStepService;

    /**
     * 获取产品详情
     */
    @GetMapping("/{id}")
    public SingleResponse<CompanyProduct> getById(@PathVariable Long id) {

        CompanyProduct companyProduct = companyProductService.getById(id);

        companyProductStatsService.incrementViewCount(companyProduct.getCompanyId(), id);

        return SingleResponse.of(companyProductService.getById(id));
    }

    /**
     * 获取产品步骤列表
     */
    @GetMapping("/steps/{productId}")
    public MultiResponse<CompanyProductStep> getStepsByProductId(@PathVariable Long productId) {

        List<CompanyProductStep> companyProductSteps = companyProductStepService.list(
                new LambdaQueryWrapper<CompanyProductStep>().eq(CompanyProductStep::getProductId, productId).orderByAsc(CompanyProductStep::getSort)
        );

        return MultiResponse.of(companyProductSteps);
    }

    /**
     * 分页查询产品列表
     */
    @PostMapping("/page")
    public MultiResponse<ProductDTO> pageQuery(@RequestBody CompanyProductPageQuery companyProductPageQuery) {

        return companyProductService.pageQueryProductDTO(companyProductPageQuery);
    }


} 