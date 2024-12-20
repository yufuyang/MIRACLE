package com.example.miracle.modules.website.service;

import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.website.dto.CompanyDTO;
import com.example.miracle.modules.website.dto.ProductDTO;

public interface WebsiteHomeService {

    /**
     * 获取轮播图列表(活动)
     */
    MultiResponse<ActivityDTO> getBanners();

    /**
     * 获取热门产品
     */
    MultiResponse<ProductDTO> getHotProducts();

    /**
     * 获取优质企业
     */
    MultiResponse<CompanyDTO> getFeaturedCompanies();
} 