package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.dto.query.ActivityPageQry;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityRegistration;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.mapper.ActivityMapper;
import com.example.miracle.modules.company.service.ActivityRegistrationService;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivityStatsService activityStatsService;

    private final ActivityRegistrationService activityRegistrationService;


    @Override
    public MultiResponse<ActivityDTO> listActivities(ActivityPageQry qry) {

        LambdaQueryWrapper<Activity> activityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (qry.getStatus() != null) {
            activityLambdaQueryWrapper.eq(Activity::getStatus, qry.getStatus());
        }
        if (qry.getCompanyId() != null) {
            activityLambdaQueryWrapper.eq(Activity::getCompanyId, qry.getCompanyId());
        }

        if (StringUtils.hasText(qry.getTitle())) {
            activityLambdaQueryWrapper.like(Activity::getTitle, qry.getTitle());
        }

        if (qry.getStartTime() != null) {
            activityLambdaQueryWrapper.ge(Activity::getStartTime, qry.getStartTime());
        }

        if (qry.getEndTime() != null) {
            activityLambdaQueryWrapper.le(Activity::getEndTime, qry.getEndTime());
        }

        if (qry.getMerchantId() != null) {
            List<ActivityRegistration> activityRegistrationList = activityRegistrationService.list(new LambdaQueryWrapper<ActivityRegistration>().eq(ActivityRegistration::getMerchantId, qry.getMerchantId()));
            List<Long> activityIds = activityRegistrationList.stream().map(ActivityRegistration::getActivityId).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(activityIds)) {
                return MultiResponse.buildSuccess();
            }

            activityLambdaQueryWrapper.in(Activity::getId, activityIds);
        }

        // 如果有排序条件,先从ActivityStats获取活动ID
        if (qry.getOrderBy() != null) {

            LambdaQueryWrapper<ActivityStats> activityStatsLambdaQueryWrapper = new LambdaQueryWrapper<>();

            if (qry.getOrderBy().equals("viewCount")) {
                activityStatsLambdaQueryWrapper.orderByDesc(ActivityStats::getViewCount);
            } else if (qry.getOrderBy().equals("registerCount")) {
                activityStatsLambdaQueryWrapper.orderByDesc(ActivityStats::getRegisterCount);
            }

            // 获取活动统计数据并排序
            Page<ActivityStats> statsPage = activityStatsService.page(new Page<>(qry.getPageNum(), qry.getPageSize()), activityStatsLambdaQueryWrapper);

            List<Long> activityIds = statsPage.getRecords().stream().map(ActivityStats::getActivityId).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(activityIds)) {
                return MultiResponse.buildSuccess();
            }

            activityLambdaQueryWrapper.in(Activity::getId, activityIds);
        }
        // 获取活动详情
        Page<Activity> activities = this.page(new Page<>(qry.getPageNum(), qry.getPageSize()), activityLambdaQueryWrapper);

        List<Long> activityIds = activities.getRecords().stream().map(Activity::getId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(activityIds)) {
            return MultiResponse.buildSuccess();
        }

        // 获取活动统计数据
        Map<Long, ActivityStats> statsMap = activityStatsService.list(new LambdaQueryWrapper<ActivityStats>().in(ActivityStats::getActivityId, activityIds)).stream().collect(Collectors.toMap(ActivityStats::getActivityId, stats -> stats));

        List<ActivityDTO> dtoList = new ArrayList<>();

        activities.getRecords().forEach(activity -> {

            ActivityDTO dto = new ActivityDTO();
            BeanUtils.copyProperties(activity, dto);

            ActivityStats stats = statsMap.get(activity.getId());
            if (stats != null) {
                dto.setViewCount(stats.getViewCount());
                dto.setRegisterCount(stats.getRegisterCount());
            }

            dtoList.add(dto);
        });
        return MultiResponse.of(dtoList, (int) activities.getTotal());
    }


} 