package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.query.CompanyProductStatsQuery;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/product/stats")
@RequiredArgsConstructor
public class CompanyProductStatsController {


    private final CompanyProductStatsService companyProductStatsService;

    private final BaseController baseController;


    /**
     * 查询产品统计
     */
    @PostMapping("/list")
    public MultiResponse<CompanyProductStats> list(@RequestBody CompanyProductStatsQuery query) {

        Long companyId = baseController.getCompanyId();
        query.setCompanyId(companyId);

        return companyProductStatsService.pageQuery(query);
    }


    /**
     * 查询产品统计
     */
    @GetMapping("/{productId}")
    public SingleResponse<CompanyProductStats> get(@PathVariable Long productId) {

        CompanyProductStats companyProductStats = companyProductStatsService.getOne(new LambdaQueryWrapper<CompanyProductStats>().eq(CompanyProductStats::getProductId, productId));

        return SingleResponse.of(companyProductStats);
    }
}
