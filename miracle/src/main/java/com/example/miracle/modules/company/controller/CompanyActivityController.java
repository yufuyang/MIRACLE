package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.dto.query.ActivityPageQry;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 活动控制器
 */
@RestController
@RequestMapping("/company/activity")
@RequiredArgsConstructor
public class CompanyActivityController {

    private final ActivityService activityService;
    private final ActivityStatsService activityStatsService;
    private final BaseController baseController;

    /**
     * 创建活动
     */
    @PostMapping
    public SingleResponse<Long> createActivity(@RequestBody Activity activity) {

        Long companyId = baseController.getCompanyId();
        activity.setCompanyId(companyId);
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
     * 更新活动
     */
    @PutMapping
    public SingleResponse updateActivity(@RequestBody Activity activity) {

        authCheck(activity.getId());

        activityService.updateById(activity);

        return SingleResponse.buildSuccess();
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public SingleResponse deleteActivity(@PathVariable Long id) {

        authCheck(id);

        activityService.removeById(id);

        activityStatsService.remove(new LambdaQueryWrapper<ActivityStats>().eq(ActivityStats::getActivityId, id));

        return SingleResponse.buildSuccess();
    }

    /**
     * 获取活动列表
     */
    @PostMapping("/list")
    public MultiResponse<ActivityDTO> listActivities(@RequestBody ActivityPageQry activityPageQry) {

        Long companyId = baseController.getCompanyId();
        activityPageQry.setCompanyId(companyId);

        return activityService.listActivities(activityPageQry);
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

    private void authCheck(Long id) {

        Activity activity = activityService.getById(id);

        if (Objects.isNull(activity)) {
            throw new BusinessException("活动不存在");
        }

        Long companyId = baseController.getCompanyId();

        if (!Objects.equals(activity.getCompanyId(), companyId)) {
            throw new BusinessException("无权操作");
        }

    }
} 