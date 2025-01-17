package com.example.miracle.modules.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.Response;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityRegistration;
import com.example.miracle.modules.company.service.ActivityRegistrationService;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import com.example.miracle.modules.merchant.dto.cmd.MerchantRegisterActivityCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant/activity/registration")
@RequiredArgsConstructor
public class MerchantActivityRegistrationController {

    private final ActivityRegistrationService activityRegistrationService;

    private final ActivityService activityService;
    private final BaseController baseController;

    private final ActivityStatsService activityStatsService;

    /**
     * 用户报名活动
     */
    @PostMapping
    public SingleResponse register(@RequestBody MerchantRegisterActivityCmd merchantRegisterActivityCmd) {

        Activity activity = activityService.getById(merchantRegisterActivityCmd.getActivityId());

        if (activity == null) {
            return SingleResponse.buildFailure("活动不存在");
        }

        if (activity.getStatus() == 2) {
            return SingleResponse.buildFailure("活动已结束");
        }

        Long merchantId = baseController.getMerchantId();
        merchantRegisterActivityCmd.setMerchantId(merchantId);

        return activityRegistrationService.register(merchantRegisterActivityCmd);
    }

    /**
     * 用户取消报名活动
     */
    @DeleteMapping("/{activityId}")
    public SingleResponse cancel(@PathVariable Long activityId) {

        Long merchantId = baseController.getMerchantId();

        activityRegistrationService.remove(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getMerchantId, merchantId));

        activityStatsService.decrementRegisterCount(activityId);

        return SingleResponse.buildSuccess();
    }

    @GetMapping("/check/{activityId}")
    public SingleResponse<Boolean> check(@PathVariable Long activityId) {
        Long merchantId = baseController.getMerchantId();

        ActivityRegistration activityRegistration = activityRegistrationService.getOne(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getMerchantId, merchantId));

        if (activityRegistration != null) {
            return SingleResponse.of(Boolean.TRUE);
        }

        return SingleResponse.of(Boolean.FALSE);
    }

}
