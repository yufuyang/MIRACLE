package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ActivityRegistrationDTO;
import com.example.miracle.modules.company.dto.cmd.ActivityRegistrationAuditCmd;
import com.example.miracle.modules.company.dto.query.ActivityRegistrationPageQry;
import com.example.miracle.modules.company.entity.ActivityRegistration;
import com.example.miracle.modules.merchant.dto.cmd.MerchantRegisterActivityCmd;

public interface ActivityRegistrationService extends IService<ActivityRegistration> {

    /**
     * 用户报名活动
     *
     * @param merchantRegisterActivityCmd 报名命令
     * @return 是否报名成功
     */
    SingleResponse register(MerchantRegisterActivityCmd merchantRegisterActivityCmd);

    /**
     * 审核报名
     *
     * @param activityRegistrationAuditCmd 审核命令
     * @return 是否审核成功
     */
    SingleResponse audit(ActivityRegistrationAuditCmd activityRegistrationAuditCmd);


    /**
     * 查询活动报名列表
     *
     * @param qry 查询条件
     * @return 报名列表
     */
    MultiResponse<ActivityRegistrationDTO> listRegistrations(ActivityRegistrationPageQry activityRegistrationPageQry);
} 