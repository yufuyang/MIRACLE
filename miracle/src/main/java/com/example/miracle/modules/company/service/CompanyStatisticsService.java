package com.example.miracle.modules.company.service;



import com.example.miracle.modules.company.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface CompanyStatisticsService {

    /**
     * 获取仪表盘数据
     */
    CompanyDashboardDTO getDashboard(Long companyId);

    /**
     * 获取趋势数据
     */
    CompanyTrendDTO getTrend(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取商户分类统计
     */
    List<CategoryStatDTO> getMerchantCategoryStats(Long companyId);

    /**
     * 获取商户区域统计
     */
    List<AreaStatDTO> getMerchantAreaStats(Long companyId);

    /**
     * 获取时段销售统计
     */
    List<HourlyStatDTO> getHourlySalesStats(Long companyId, LocalDate date);

    /**
     * 获取商户销售额区间统计
     */
    List<SalesRangeStatDTO> getMerchantSalesRangeStats(Long companyId, LocalDate startDate);

    /**
     * 获取商户排行
     */
    List<MerchantRankDTO> getTopMerchants(Long companyId, Integer limit, LocalDate startDate);

    /**
     * 获取商品排行
     */
    List<ProductRankDTO> getTopProducts(Long companyId, Integer limit, LocalDate startDate);

    /**
     * 获取每日统计数据
     */
    DailyStatDTO getDailyStats(Long companyId, LocalDate date);
}