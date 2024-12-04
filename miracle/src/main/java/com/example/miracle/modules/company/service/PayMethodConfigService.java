package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.dto.PayMethodConfigDTO;
import com.example.miracle.modules.company.entity.PayMethodConfig;

import java.util.List;

public interface PayMethodConfigService extends IService<PayMethodConfig> {

    /**
     * 获取商户可用的支付方式列表
     */
    List<PayMethodConfig> getEnabledMethods(Long merchantId);

    /**
     * 保存支付方式配置
     */
    void saveConfig(PayMethodConfigDTO dto);

    /**
     * 更新支付方式状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除支付方式配置
     */
    void deleteConfig(Long id);
}
