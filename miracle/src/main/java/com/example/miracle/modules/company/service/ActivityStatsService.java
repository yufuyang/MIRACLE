package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.ActivityStatsDTO;
import com.example.miracle.modules.company.dto.ActivityTrendDTO;
import com.example.miracle.modules.company.entity.ActivityStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ActivityStatsService extends IService<ActivityStats> {
    
    /**
     * 获取活动统计概览
     */
    ActivityStatsDTO getStatsOverview(Long companyId);

    /**
     * 获取活动趋势数据
     */
    ActivityTrendDTO getStatsTrend(Long companyId, Long activityId, Integer days);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long activityId);

    /**
     * 增加报名数
     */
    void incrementRegisterCount(Long activityId);

    /**
     * 减少报名数
     */
    void decrementRegisterCount(Long activityId);

    /**
     * 获取热门活动排行
     * @param companyId 企业ID
     * @param type 排序类型：view-按浏览量，register-按报名数
     * @return 热门活动列表
     */
    List<Map<String, Object>> getHotActivities(Long companyId, String type);
} 