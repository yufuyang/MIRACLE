package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.dto.ActivityStatsDTO;
import com.example.miracle.modules.company.dto.ActivityTrendDTO;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.mapper.ActivityStatsMapper;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        ActivityTrendDTO dto = new ActivityTrendDTO();
        List<String> dates = new ArrayList<>();
        List<Integer> viewCounts = new ArrayList<>();
        List<Integer> regCounts = new ArrayList<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        // 获取日期范围内的统计数据
        List<ActivityStats> statsList = baseMapper.getStatsByDateRange(activityId, startDate, endDate);
        
        // 将统计数据转换为Map，以日期为key
        Map<LocalDate, ActivityStats> statsMap = statsList.stream()
            .collect(Collectors.toMap(
                ActivityStats::getStatsDate,
                stats -> stats
            ));
        
        // 填充每天的数据
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.format(formatter));
            ActivityStats stats = statsMap.get(date);
            viewCounts.add(stats != null ? stats.getViewCount() : 0);
            regCounts.add(stats != null ? stats.getRegisterCount() : 0);
        }
        
        dto.setDates(dates);
        dto.setViewCounts(viewCounts);
        dto.setRegCounts(regCounts);
        
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
    public MultiResponse<ActivityDTO> getHotActivities(Long companyId, String type) {
        return MultiResponse.of(baseMapper.selectHotActivities(companyId, type));
    }
} 