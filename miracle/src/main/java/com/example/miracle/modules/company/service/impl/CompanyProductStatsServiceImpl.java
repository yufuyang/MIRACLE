package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ProductRankDTO;
import com.example.miracle.modules.company.dto.ProductStatsDTO;
import com.example.miracle.modules.company.dto.ProductTrendDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductStatsQuery;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.mapper.CompanyProductStatsMapper;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.website.dto.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 产品统计服务实现类
 */
@Service
public class CompanyProductStatsServiceImpl extends ServiceImpl<CompanyProductStatsMapper, CompanyProductStats> implements CompanyProductStatsService {

    @Override
    public MultiResponse<CompanyProductStats> pageQuery(CompanyProductStatsQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<CompanyProductStats> wrapper = new LambdaQueryWrapper<CompanyProductStats>()
                .eq(query.getCompanyId() != null, CompanyProductStats::getCompanyId, query.getCompanyId())
                .eq(query.getProductId() != null, CompanyProductStats::getProductId, query.getProductId());

        // 添加排序
        if (query.getOrderField() != null) {
            switch (query.getOrderField()) {
                case "viewCount":
                    wrapper.orderBy(true, query.getAsc(), CompanyProductStats::getViewCount);
                    break;
                case "intentionCount":
                    wrapper.orderBy(true, query.getAsc(), CompanyProductStats::getIntentionCount);
                    break;
                default:
                    wrapper.orderByDesc(CompanyProductStats::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(CompanyProductStats::getCreateTime);
        }

        // 执行分页查询
        Page<CompanyProductStats> page = this.page(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper
        );

        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }

    @Override
    public List<Map<String, Object>> selectTopCompanies(int limit) {
        return this.baseMapper.selectTopCompanies(limit);
    }

    @Override
    public List<ProductDTO> selectHotProducts(int limit) {
        return this.baseMapper.selectHotProducts(limit);
    }


    @Override
    public SingleResponse<ProductStatsDTO> getOverview(Long companyId) {
        ProductStatsDTO productStatsDTO = new ProductStatsDTO();

        // 获取今日数据
        Map<String, Long> todayStats = getBaseMapper().getTodayStats(companyId);
        productStatsDTO.setTodayViews(todayStats.get("views"));
        productStatsDTO.setTodayIntentions(todayStats.get("intentions"));

        // 获取总计数据
        Map<String, Long> totalStats = getBaseMapper().getTotalStats(companyId);
        productStatsDTO.setTotalViews(totalStats.get("views"));
        productStatsDTO.setTotalIntentions(totalStats.get("intentions"));

        return SingleResponse.of(productStatsDTO);
    }

    @Override
    public SingleResponse<ProductTrendDTO> getTrend(Long productId, String timeRange, String dataType) {
        ProductTrendDTO productTrendDTO = new ProductTrendDTO();

        // 计算日期范围
        LocalDate endDate = LocalDate.now();

        LocalDate startDate = timeRange.equals("week") ? endDate.minusDays(6) : endDate.minusDays(29);

        // 获取日期范围内的统计数据
        List<CompanyProductStats> statsList = getBaseMapper().getStatsByDateRange(productId, startDate, endDate);

        // 构建日期列表和数据
        List<String> dates = new ArrayList<>();

        Map<LocalDate, CompanyProductStats> statsMap = statsList.stream().collect(Collectors.toMap(CompanyProductStats::getStatsDate, Function.identity()));

        List<Long> views = new ArrayList<>();
        List<Long> intentions = new ArrayList<>();

        // 填充数据，确保连续性
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            CompanyProductStats stats = statsMap.getOrDefault(date, new CompanyProductStats());
            views.add(stats.getViewCount() != null ? stats.getViewCount().longValue() : 0L);
            intentions.add(stats.getIntentionCount() != null ? stats.getIntentionCount().longValue() : 0L);
        }

        productTrendDTO.setDates(dates);
        productTrendDTO.setViews(views);
        productTrendDTO.setIntentions(intentions);

        return SingleResponse.of(productTrendDTO);
    }

    @Override
    public MultiResponse<ProductRankDTO> getHotProducts(Long companyId, String timeRange, String rankType) {
        // 获取当前热门产品
        List<ProductDTO> currentProducts = getBaseMapper().getCompanyHotProducts(companyId, rankType);

        // 计算上期时间范围
        LocalDate now = LocalDate.now();

        LocalDate lastEndDate, lastStartDate;

        if ("week".equals(timeRange)) {
            lastEndDate = now.minusDays(7);
            lastStartDate = lastEndDate.minusDays(6);
        } else { // month
            lastEndDate = now.withDayOfMonth(1).minusDays(1);
            lastStartDate = lastEndDate.withDayOfMonth(1);
        }

        // 获取上期数据
        List<Map<String, Object>> lastPeriodStats = getBaseMapper().getLastPeriodStats(companyId, lastStartDate, lastEndDate);

        // 将上期数据转换为Map，方便查找
        Map<Long, Map<String, Object>> lastStatsMap = lastPeriodStats.stream().collect(Collectors.toMap(m -> ((Number) m.get("product_id")).longValue(), Function.identity()));


        List<ProductRankDTO> productRankDTOList = currentProducts.stream().map(product -> {
            ProductRankDTO productRankDTO = new ProductRankDTO();
            productRankDTO.setId(product.getId());
            productRankDTO.setProductName(product.getProductName());
            productRankDTO.setViews(product.getViewCount().longValue());
            productRankDTO.setIntentions(product.getIntentionCount().longValue());

            // 获取上期数据
            Map<String, Object> lastStats = lastStatsMap.get(product.getId());
            if (lastStats != null) {
                productRankDTO.setLastViews(((Number) lastStats.get("lastViews")).longValue());
                productRankDTO.setLastIntentions(((Number) lastStats.get("lastIntentions")).longValue());
            } else {
                productRankDTO.setLastViews(0L);
                productRankDTO.setLastIntentions(0L);
            }

            return productRankDTO;
        }).collect(Collectors.toList());


        // 构建返回数据
        return MultiResponse.of(productRankDTOList);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long companyId, Long productId) {

        CompanyProductStats stats = new CompanyProductStats();
        stats.setCompanyId(companyId);
        stats.setProductId(productId);
        stats.setStatsDate(LocalDate.now());
        stats.setViewCount(1);
        stats.setIntentionCount(0);

        getBaseMapper().updateStats(stats);
    }

    @Override
    @Transactional
    public void incrementIntentCount(Long companyId, Long productId) {
        CompanyProductStats stats = new CompanyProductStats();
        stats.setCompanyId(companyId);
        stats.setProductId(productId);
        stats.setStatsDate(LocalDate.now());
        stats.setViewCount(0);
        stats.setIntentionCount(1);

        getBaseMapper().updateStats(stats);
    }

    @Override
    public void decrementIntentCount(Long productId) {
        getBaseMapper().decrementIntentCount(productId);
    }

}
