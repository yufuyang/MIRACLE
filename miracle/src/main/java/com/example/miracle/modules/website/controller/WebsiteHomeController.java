package com.example.miracle.modules.website.controller;

import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.website.dto.CompanyDTO;
import com.example.miracle.modules.website.dto.ProductDTO;
import com.example.miracle.modules.website.service.WebsiteHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 网站首页控制器
 */
@RestController
@RequestMapping("/website/home")
@RequiredArgsConstructor
public class WebsiteHomeController {

    private final WebsiteHomeService websiteHomeService;

    /**
     * 获取轮播图列表(活动)
     */
    @GetMapping("/banners")
    public MultiResponse<ActivityDTO> getBanners() {
        return websiteHomeService.getBanners();
    }

    /**
     * 获取热门产品
     */
    @GetMapping("/hot-products")
    public MultiResponse<ProductDTO> getHotProducts() {
        return websiteHomeService.getHotProducts();
    }

    /**
     * 获取优质企业
     */
    @GetMapping("/featured-companies")
    public MultiResponse<CompanyDTO> getFeaturedCompanies() {
        return websiteHomeService.getFeaturedCompanies();
    }
} 