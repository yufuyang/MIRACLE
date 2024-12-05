package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.PayMethodConfig;
import com.example.miracle.modules.company.mapper.PayMethodConfigMapper;
import com.example.miracle.modules.company.service.PayMethodConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayMethodConfigServiceImpl extends ServiceImpl<PayMethodConfigMapper, PayMethodConfig> implements PayMethodConfigService {
    
    @Override
    public List<PayMethodConfig> getMerchantPayMethods(Long merchantId) {
        return this.lambdaQuery()
                .eq(PayMethodConfig::getMerchantId, merchantId)
                .eq(PayMethodConfig::getStatus, 1)
                .orderByAsc(PayMethodConfig::getSort)
                .list();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        PayMethodConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("支付方式不存在");
        }
        
        config.setStatus(status);
        this.updateById(config);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Long id, String config) {
        PayMethodConfig payMethod = this.getById(id);
        if (payMethod == null) {
            throw new BusinessException("支付方式不存在");
        }
        
        payMethod.setConfig(config);
        this.updateById(payMethod);
    }
}