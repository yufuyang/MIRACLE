package com.example.miracle.modules.website.controller;

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
@RequestMapping("/website/company/product/stats")
@RequiredArgsConstructor
public class WebsiteCompanyProductStatsController {


    private final CompanyProductStatsService companyProductStatsService;


    /**
     * 查询产品统计
     */
    @GetMapping("/{productId}")
    public SingleResponse<CompanyProductStats> get(@PathVariable Long productId) {

        return companyProductStatsService.get(productId);
    }
}
