package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.PayMethodConfig;

import java.util.List;

public interface PayMethodConfigService extends IService<PayMethodConfig> {

    /**
     * 获取商户支付方式列表
     */
    List<PayMethodConfig> getMerchantPayMethods(Long merchantId);

    /**
     * 更新支付方式状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 更新支付方式配置
     */
    void updateConfig(Long id, String config);
}
