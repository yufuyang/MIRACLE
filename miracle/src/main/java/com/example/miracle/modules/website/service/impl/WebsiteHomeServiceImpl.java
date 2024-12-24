package com.example.miracle.modules.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.platform.entity.Company;
import com.example.miracle.modules.platform.service.CompanyService;
import com.example.miracle.modules.website.dto.CompanyDTO;
import com.example.miracle.modules.website.dto.ProductDTO;
import com.example.miracle.modules.website.service.WebsiteHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebsiteHomeServiceImpl implements WebsiteHomeService {

    private final CompanyService companyService;
    private final CompanyProductService companyProductService;
    private final CompanyProductStatsService companyProductStatsService;
    private final ActivityService activityService;
    private final ActivityStatsService activityStatsService;

    @Override
    public MultiResponse<ActivityDTO> getBanners() {
        // 获取最新的活动作为轮播图
        // 1. 先获取平台活动
        List<Activity> platformActivities = activityService.list(new LambdaQueryWrapper<Activity>().eq(Activity::getCompanyId, 0L).ge(Activity::getEndTime, LocalDateTime.now()).orderByAsc(Activity::getStartTime).last("LIMIT 3"));

        // 2. 如果平台活动不足3个,获取企业活动补充
        List<Activity> activities = platformActivities;

        if (platformActivities.size() < 3) {
            List<Activity> companyActivities = activityService.list(
                    new LambdaQueryWrapper<Activity>()
                            .ne(Activity::getCompanyId, 0L)
                            .ge(Activity::getEndTime, LocalDateTime.now())
                            .orderByAsc(Activity::getStartTime)
                            .last("LIMIT " + (3 - platformActivities.size()))
            );
            activities.addAll(companyActivities);
        }

        if (activities.isEmpty()) {
            return MultiResponse.buildSuccess();
        }

        // 获取活动统计数据
        Map<Long, ActivityStats> statsMap = activityStatsService.list(new LambdaQueryWrapper<ActivityStats>().in(ActivityStats::getActivityId, activities.stream().map(Activity::getId).collect(Collectors.toList()))).stream().collect(Collectors.toMap(ActivityStats::getActivityId, stats -> stats));

        // 转换为DTO
        List<ActivityDTO> dtoList = activities.stream().map(activity -> {
            ActivityDTO dto = new ActivityDTO();
            BeanUtils.copyProperties(activity, dto);

            ActivityStats stats = statsMap.get(activity.getId());
            if (stats != null) {
                dto.setViewCount(stats.getViewCount());
                dto.setRegisterCount(stats.getRegisterCount());
            }

            return dto;
        }).collect(Collectors.toList());

        return MultiResponse.of(dtoList);
    }

    @Override
    public MultiResponse<ProductDTO> getHotProducts() {
        // 使用联合查询获取热门产品（已上架的前6个）
        List<ProductDTO> dtoList = companyProductStatsService.selectHotProducts(6);
        return MultiResponse.of(dtoList);
    }

    @Override
    public MultiResponse<CompanyDTO> getFeaturedCompanies() {
        // 直接查询前6个优质企业
        List<Map<String, Object>> topCompanies = companyProductStatsService.selectTopCompanies(6);

        if (topCompanies.isEmpty()) {
            return MultiResponse.buildSuccess();
        }

        // 获取企业ID列表
        List<Long> companyIds = topCompanies.stream().map(map -> ((Number) map.get("company_id")).longValue()).collect(Collectors.toList());

        // 获取企业详情
        Map<Long, Company> companyMap = companyService.list(new LambdaQueryWrapper<Company>().in(Company::getId, companyIds)).stream().collect(Collectors.toMap(Company::getId, company -> company));

        // 转换为DTO并保持排序
        List<CompanyDTO> dtoList = topCompanies.stream()
            .map(stats -> {
                Long companyId = ((Number) stats.get("company_id")).longValue();
                Company company = companyMap.get(companyId);
                if (company == null) {
                    return null;
                }

                CompanyDTO dto = new CompanyDTO();
                BeanUtils.copyProperties(company, dto);
                dto.setProductCount(((Number) stats.get("product_count")).intValue());
                dto.setIntentionCount(((Number) stats.get("total_intention_count")).intValue());

                return dto;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());

        return MultiResponse.of(dtoList);
    }
} 