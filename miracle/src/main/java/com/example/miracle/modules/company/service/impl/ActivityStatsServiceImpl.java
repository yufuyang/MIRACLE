package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.dto.ActivityStatsDTO;
import com.example.miracle.modules.company.dto.ActivityTrendDTO;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.mapper.ActivityStatsMapper;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityStatsServiceImpl extends ServiceImpl<ActivityStatsMapper, ActivityStats> implements ActivityStatsService {

    @Override
    public ActivityStatsDTO getStatsOverview(Long companyId) {
        Map<String, Object> stats = baseMapper.selectStatsOverview(companyId);
        
        ActivityStatsDTO dto = new ActivityStatsDTO();
        dto.setTotalCount(((Number) stats.get("totalCount")).intValue());
        dto.setActiveCount(((Number) stats.get("activeCount")).intValue());
        dto.setViewCount(((Number) stats.get("viewCount")).intValue());
        dto.setRegisterCount(((Number) stats.get("registerCount")).intValue());
        
        return dto;
    }

    @Override
    public ActivityTrendDTO getStatsTrend(Long companyId, Long activityId, Integer days) {
        List<Map<String, Object>> trendData = baseMapper.selectStatsTrend(companyId, activityId, days);
        
        ActivityTrendDTO dto = new ActivityTrendDTO();
        dto.setDates(trendData.stream().map(m -> (String) m.get("date")).collect(Collectors.toList()));
        dto.setViewCounts(trendData.stream().map(m -> ((Number) m.get("viewCount")).intValue()).collect(Collectors.toList()));
        dto.setRegCounts(trendData.stream().map(m -> ((Number) m.get("registerCount")).intValue()).collect(Collectors.toList()));
        
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long activityId) {
        LocalDate today = LocalDate.now();

        ActivityStats stats = this.lambdaQuery().eq(ActivityStats::getActivityId, activityId).eq(ActivityStats::getStatsDate, today).one();
        
        if (stats == null) {
            stats = new ActivityStats();
            stats.setActivityId(activityId);
            stats.setStatsDate(today);
            stats.setViewCount(1);
            stats.setRegisterCount(0);
            this.save(stats);
        } else {
            stats.setViewCount(stats.getViewCount() + 1);
            this.updateById(stats);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementRegisterCount(Long activityId) {
        LocalDate today = LocalDate.now();

        ActivityStats stats = this.lambdaQuery().eq(ActivityStats::getActivityId, activityId).eq(ActivityStats::getStatsDate, today).one();
        
        if (stats == null) {
            stats = new ActivityStats();
            stats.setActivityId(activityId);
            stats.setStatsDate(today);
            stats.setViewCount(0);
            stats.setRegisterCount(1);
            this.save(stats);
        } else {
            stats.setRegisterCount(stats.getRegisterCount() + 1);
            this.updateById(stats);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementRegisterCount(Long activityId) {
        LocalDate today = LocalDate.now();

        ActivityStats stats = this.lambdaQuery().eq(ActivityStats::getActivityId, activityId).eq(ActivityStats::getStatsDate, today).one();
        
        if (stats != null && stats.getRegisterCount() > 0) {
            stats.setRegisterCount(stats.getRegisterCount() - 1);
            this.updateById(stats);
        }
    }

    @Override
    public List<Map<String, Object>> getHotActivities(Long companyId, String type) {
        return baseMapper.selectHotActivities(companyId, type);
    }
} 