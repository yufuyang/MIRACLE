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
@RequestMapping("/website/product")
@RequiredArgsConstructor
public class WebsiteCompanyProductController {


    private final CompanyProductService companyProductService;

    private final CompanyProductStatsService companyProductStatsService;


    /**
     * 获取产品详情
     */
    @GetMapping("/{id}")
    public SingleResponse<CompanyProduct> getById(@PathVariable Long id) {

        CompanyProductStats companyProductStats = companyProductStatsService.getOne(new LambdaQueryWrapper<CompanyProductStats>().eq(CompanyProductStats::getProductId, id));

        if (Objects.nonNull(companyProductStats)) {
            companyProductStats.setViewCount(companyProductStats.getViewCount() + 1);
            companyProductStatsService.updateById(companyProductStats);
        }

        return SingleResponse.of(companyProductService.getById(id));
    }

    /**
     * 分页查询产品列表
     */
    @PostMapping("/page")
    public MultiResponse<CompanyProduct> pageQuery(@RequestBody CompanyProductPageQuery companyProductPageQuery) {


        return companyProductService.pageQuery(companyProductPageQuery);
    }


} 