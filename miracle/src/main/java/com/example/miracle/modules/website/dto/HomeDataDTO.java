package com.example.miracle.modules.website.dto;

import com.example.miracle.modules.platform.entity.Company;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HomeDataDTO {
    
    /**
     * 轮播图列表(活动)
     */
    private List<BannerDTO> banners;
    
    /**
     * 热门产品推荐
     */
    private List<ProductDTO> hotProducts;
    
    /**
     * 优质企业展示
     */
    private List<CompanyDTO> featuredCompanies;

} 