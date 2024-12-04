package com.example.miracle.modules.company.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.dto.PayMethodConfigDTO;
import com.example.miracle.modules.company.entity.PayMethodConfig;
import com.example.miracle.modules.company.mapper.PayMethodConfigMapper;
import com.example.miracle.modules.company.service.PayMethodConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayMethodConfigServiceImpl extends ServiceImpl<PayMethodConfigMapper, PayMethodConfig> implements PayMethodConfigService {


    @Override
    public List<PayMethodConfig> getEnabledMethods(Long merchantId) {
        return this.lambdaQuery()
                .eq(PayMethodConfig::getMerchantId, merchantId)
                .eq(PayMethodConfig::getStatus, 1)
                .orderByAsc(PayMethodConfig::getSort)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(PayMethodConfigDTO dto) {
        // 检查是否已存在
        boolean exists = this.lambdaQuery()
                .eq(PayMethodConfig::getMerchantId, dto.getMerchantId())
                .eq(PayMethodConfig::getPayType, dto.getPayType())
                .exists();

        if (exists) {
            throw new BusinessException("该支付方式已配置");
        }

        // 保存配置
        PayMethodConfig config = new PayMethodConfig();
        BeanUtils.copyProperties(dto, config);

        // 转换配置信息为JSON
        if (dto.getConfig() != null) {
            config.setConfig(JSONUtil.toJsonStr(dto.getConfig()));
        }

        this.save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        PayMethodConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        config.setStatus(status);
        this.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        this.removeById(id);
    }
}