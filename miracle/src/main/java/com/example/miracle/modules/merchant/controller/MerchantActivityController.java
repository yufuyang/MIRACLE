package com.example.miracle.modules.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.ActivityDTO;
import com.example.miracle.modules.company.dto.query.ActivityPageQry;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityStats;
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
@RequestMapping("/merchant/activity")
@RequiredArgsConstructor
public class MerchantActivityController {

    private final ActivityService activityService;
    private final ActivityStatsService activityStatsService;
    private final BaseController baseController;


    /**
     * 获取活动列表
     */
    @GetMapping
    public MultiResponse<ActivityDTO> listActivities(@RequestBody ActivityPageQry activityPageQry) {

        Long merchantId = baseController.getMerchantId();

        activityPageQry.setMerchantId(merchantId);

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


} 