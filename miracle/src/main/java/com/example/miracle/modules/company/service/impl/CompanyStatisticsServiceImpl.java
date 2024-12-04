package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.modules.company.dto.*;
import com.example.miracle.modules.company.entity.Merchant;
import com.example.miracle.modules.company.mapper.CompanyStatisticsMapper;
import com.example.miracle.modules.company.service.CompanyStatisticsService;
import com.example.miracle.modules.company.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyStatisticsServiceImpl implements CompanyStatisticsService {

    private final MerchantService merchantService;
    private final CompanyStatisticsMapper statisticsMapper;

    @Override
    public List<CategoryStatDTO> getMerchantCategoryStats(Long companyId) {
        return statisticsMapper.getMerchantCategoryStats(companyId);
    }

    @Override
    public List<AreaStatDTO> getMerchantAreaStats(Long companyId) {
        return statisticsMapper.getMerchantAreaStats(companyId);
    }

    @Override
    public List<HourlyStatDTO> getHourlySalesStats(Long companyId, LocalDate date) {
        return statisticsMapper.getHourlySalesStats(companyId, date);
    }

    @Override
    public List<SalesRangeStatDTO> getMerchantSalesRangeStats(Long companyId, LocalDate startDate) {
        LocalDateTime startTime;
        if (startDate != null) {
            startTime = startDate.atStartOfDay();
        } else {
            // 默认统计本月
            startTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }
        return statisticsMapper.getMerchantSalesRangeStats(companyId, startTime);
    }

    @Override
    public List<MerchantRankDTO> getTopMerchants(Long companyId, Integer limit, LocalDate startDate) {
        LocalDateTime startTime;
        if (startDate != null) {
            startTime = startDate.atStartOfDay();
        } else {
            // 默认统计本月
            startTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }
        return statisticsMapper.getTopMerchants(companyId, startTime, limit);
    }

    @Override
    public List<ProductRankDTO> getTopProducts(Long companyId, Integer limit, LocalDate startDate) {
        LocalDateTime startTime;
        if (startDate != null) {
            startTime = startDate.atStartOfDay();
        } else {
            // 默认统计本月
            startTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }
        return statisticsMapper.getTopProducts(companyId, startTime, limit);
    }

    @Override
    public DailyStatDTO getDailyStats(Long companyId, LocalDate date) {

        DailyStatDTO dailyStats = new DailyStatDTO();

        // 获取商户数量
        dailyStats.setMerchantCount(statisticsMapper.getDailyMerchantCount(companyId, date));

        // 获取销售额
        dailyStats.setSalesAmount(statisticsMapper.getDailySalesAmount(companyId, date));

        // 获取订单数
        dailyStats.setOrderCount(statisticsMapper.getDailyOrderCount(companyId, date));

        // 计算平均客单价
        if (dailyStats.getOrderCount() > 0 && dailyStats.getSalesAmount() != null) {
            dailyStats.setAverageOrderAmount(
                    dailyStats.getSalesAmount()
                            .divide(new BigDecimal(dailyStats.getOrderCount()), 2, BigDecimal.ROUND_HALF_UP)
            );
        } else {
            dailyStats.setAverageOrderAmount(BigDecimal.ZERO);
        }

        return dailyStats;
    }

    @Override
    public CompanyDashboardDTO getDashboard(Long companyId) {

        CompanyDashboardDTO dashboard = new CompanyDashboardDTO();

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // 获取商户总数
        dashboard.setMerchantCount(statisticsMapper.getDailyMerchantCount(companyId, LocalDate.now()));

        // 获取待审核商户数
        dashboard.setPendingMerchantCount(statisticsMapper.getPendingMerchantCount(companyId));

        // 获取本月新增商户数
        dashboard.setNewMerchantCount(statisticsMapper.getNewMerchantCount(companyId, monthStart));

        // 获取本月活跃商户数
        dashboard.setActiveMerchantCount(statisticsMapper.getActiveMerchantCount(companyId, monthStart));

        // 获取商户排名
        dashboard.setTopMerchants(statisticsMapper.getTopMerchants(companyId, monthStart, 5));

        // 获取商品排名
        dashboard.setTopProducts(statisticsMapper.getTopProducts(companyId, monthStart, 5));

        return dashboard;
    }

    @Override
    public CompanyTrendDTO getTrend(Long companyId, LocalDate startDate, LocalDate endDate) {

        CompanyTrendDTO trend = new CompanyTrendDTO();

        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> merchantCountList = new ArrayList<>();
        List<BigDecimal> salesAmountList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate);

            // 获取每日商户数
            merchantCountList.add(statisticsMapper.getDailyMerchantCount(companyId, currentDate));

            // 获取每日销售额
            salesAmountList.add(statisticsMapper.getDailySalesAmount(companyId, currentDate));

            // 获取每日订单数
            orderCountList.add(statisticsMapper.getDailyOrderCount(companyId, currentDate));

            currentDate = currentDate.plusDays(1);
        }

        trend.setDateList(dateList);
        trend.setMerchantCountList(merchantCountList);
        trend.setSalesAmountList(salesAmountList);
        trend.setOrderCountList(orderCountList);

        return trend;
    }
}