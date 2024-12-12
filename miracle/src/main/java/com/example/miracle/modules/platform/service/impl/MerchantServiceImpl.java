package com.example.miracle.modules.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.platform.dto.query.MerchantPageQuery;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.platform.mapper.MerchantMapper;
import com.example.miracle.modules.platform.service.MerchantService;
import com.example.miracle.modules.merchant.service.MerchantUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商户信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final MerchantUserService merchantUserService;

    @Override
    public MultiResponse<Merchant> pageQuery(MerchantPageQuery merchantPageQuery) {
        // 构建查询条件
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .like(StringUtils.isNotBlank(merchantPageQuery.getMerchantName()), Merchant::getMerchantName, merchantPageQuery.getMerchantName())
                .like(StringUtils.isNotBlank(merchantPageQuery.getContactName()), Merchant::getContactName, merchantPageQuery.getContactName())
                .eq(merchantPageQuery.getStatus() != null, Merchant::getStatus, merchantPageQuery.getStatus())
                .orderByDesc(Merchant::getCreateTime);

        // 执行分页查询
        Page<Merchant> page = this.page(new Page<>(merchantPageQuery.getPageNum(), merchantPageQuery.getPageSize()), wrapper);

        // 返回结果
        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Merchant merchant) {
        // 保存商户信息
        boolean result = super.save(merchant);
        if (result) {
            // 创建商户管理员
            MerchantUser merchantUser = new MerchantUser();
            merchantUser.setMerchantId(merchant.getId());
            merchantUser.setUsername(merchant.getContactPhone());
            merchantUser.setRealName(merchant.getContactName());
            merchantUser.setPhone(merchant.getContactPhone());
            merchantUser.setPassword("123456");
            merchantUser.setStatus(1);
            merchantUserService.save(merchantUser);
        }
        return result;
    }
} 