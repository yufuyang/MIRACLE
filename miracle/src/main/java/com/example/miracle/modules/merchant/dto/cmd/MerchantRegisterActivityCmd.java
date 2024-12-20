package com.example.miracle.modules.merchant.dto.cmd;

import lombok.Data;

@Data
public class MerchantRegisterActivityCmd {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 用户ID
     */
    private Long merchantId;
}
