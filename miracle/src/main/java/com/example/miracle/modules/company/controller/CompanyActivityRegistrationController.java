package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ActivityRegistrationDTO;
import com.example.miracle.modules.company.dto.cmd.ActivityRegistrationAuditCmd;
import com.example.miracle.modules.company.dto.query.ActivityRegistrationPageQry;
import com.example.miracle.modules.company.service.ActivityRegistrationService;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 活动报名控制器
 */
@RestController
@RequestMapping("/company/activity/registration")
@RequiredArgsConstructor
public class CompanyActivityRegistrationController {

    private final ActivityRegistrationService activityRegistrationService;

    private final ActivityStatsService activityStatsService;


    /**
     * 审核报名
     */
    @PutMapping("/{id}/audit")
    public SingleResponse audit(@RequestBody ActivityRegistrationAuditCmd activityRegistrationAuditCmd) {

        if (activityRegistrationAuditCmd.getStatus() != 1 && activityRegistrationAuditCmd.getStatus() != 2) {
            return SingleResponse.buildFailure("无效的审核状态");
        }

        activityRegistrationService.audit(activityRegistrationAuditCmd);



        return activityRegistrationService.audit(activityRegistrationAuditCmd);
    }

    /**
     * 获取报名列表
     */
    @GetMapping
    public MultiResponse<ActivityRegistrationDTO> listRegistrations(@RequestBody ActivityRegistrationPageQry activityRegistrationPageQry){
        return activityRegistrationService.listRegistrations(activityRegistrationPageQry);
    }


} 