package com.example.miracle.modules.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.dto.query.ActivityPageQry;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端活动管理控制器
 */
@RestController
@RequestMapping("/platform/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityStatsService activityStatsService;

    /**
     * 创建平台活动
     */
    @PostMapping
    public SingleResponse<Long> createActivity(@RequestBody ActivityDTO dto) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(dto, activity);
        // 设置为平台活动(companyId为0)
        activity.setCompanyId(0L);
        activityService.save(activity);

        // 创建活动统计记录
        ActivityStats stats = new ActivityStats();
        stats.setActivityId(activity.getId());
        stats.setViewCount(0);
        stats.setRegisterCount(0);
        activityStatsService.save(stats);

        return SingleResponse.of(activity.getId());
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public SingleResponse deleteActivity(@PathVariable Long id) {

        activityService.removeById(id);

        activityStatsService.remove(new LambdaQueryWrapper<ActivityStats>().eq(ActivityStats::getActivityId, id));

        return SingleResponse.buildSuccess();
    }

    /**
     * 获取活动列表
     */
    @GetMapping
    public MultiResponse<ActivityDTO> listActivities(@RequestBody ActivityPageQry qry) {

        return activityService.listActivities(qry);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public SingleResponse<ActivityDTO> getActivity(@PathVariable Long id) {

        Activity activity = activityService.getById(id);

        if (activity == null) {
            return SingleResponse.buildFailure("活动不存在");
        }

        ActivityDTO dto = new ActivityDTO();
        BeanUtils.copyProperties(activity, dto);

        // 获取统计数据
        ActivityStats stats = activityStatsService.getOne(new LambdaQueryWrapper<ActivityStats>().eq(ActivityStats::getActivityId, id));
        if (stats != null) {
            dto.setViewCount(stats.getViewCount());
            dto.setRegisterCount(stats.getRegisterCount());
        }

        return SingleResponse.of(dto);
    }


} 