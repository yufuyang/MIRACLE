package com.example.miracle.modules.company.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ActivityStatsDTO;
import com.example.miracle.modules.company.dto.ActivityTrendDTO;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * 企业活动统计控制器
 */
@RestController
@RequestMapping("/company/activity/stats")
@RequiredArgsConstructor
public class CompanyActivityStatsController {

    private final BaseController baseController;

    private final ActivityStatsService activityStatsService;

    /**
     * 获取活动统计概览数据
     */
    @GetMapping("/overview")
    public SingleResponse<ActivityStatsDTO> getStatsOverview() {
        Long companyId = baseController.getCompanyId();

        ActivityStatsDTO stats = activityStatsService.getStatsOverview(companyId);
        return SingleResponse.of(stats);
    }

    /**
     * 获取活动趋势数据
     */
    @GetMapping("/trend")
    public SingleResponse<ActivityTrendDTO> getStatsTrend(@RequestParam(required = false) Long activityId, @RequestParam Integer days) {

        Long companyId = baseController.getCompanyId();

        ActivityTrendDTO trend = activityStatsService.getStatsTrend(companyId, activityId, days);
        return SingleResponse.of(trend);
    }

    /**
     * 获取热门活动排行
     */
    @GetMapping("/hot")
    public SingleResponse<List<Map<String, Object>>> getHotActivities(@RequestParam(defaultValue = "view") String type) {

        if (!Arrays.asList("view", "register").contains(type)) {
            throw new IllegalArgumentException("Invalid type parameter");
        }

        Long companyId = baseController.getCompanyId();

        List<Map<String, Object>> hotActivities = activityStatsService.getHotActivities(companyId, type);

        return SingleResponse.of(hotActivities);
    }
} 