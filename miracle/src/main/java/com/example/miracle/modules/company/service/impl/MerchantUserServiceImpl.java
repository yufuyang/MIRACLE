package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;

import com.example.miracle.modules.company.entity.MerchantUser;
import com.example.miracle.modules.company.mapper.MerchantUserMapper;
import com.example.miracle.modules.company.service.MerchantUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantUserServiceImpl extends ServiceImpl<MerchantUserMapper, MerchantUser> implements MerchantUserService {



    @Override
    public Page<MerchantUser> pageMerchantUser(Long merchantId, Integer current, Integer size) {
        return this.page(new Page<>(current, size),
                new LambdaQueryWrapper<MerchantUser>()
                        .eq(MerchantUser::getMerchantId, merchantId)
                        .orderByDesc(MerchantUser::getCreateTime));
    }

    @Override
    public void createMerchantAdmin(Long merchantId, MerchantUser merchantUser) {
        // 验证用户名是否存在
        if (this.count(new LambdaQueryWrapper<MerchantUser>()
                .eq(MerchantUser::getUsername, merchantUser.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 设置商户ID
        merchantUser.setMerchantId(merchantId);
        // 加密密码
        merchantUser.setPassword(merchantUser.getPassword());
        // 设置状态
        merchantUser.setStatus(1);

        this.save(merchantUser);
    }

    @Override
    public void updateMerchantUserStatus(Long userId, Integer status) {
        MerchantUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public void resetPassword(Long userId) {
        MerchantUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 重置为默认密码
        user.setPassword("123456");
        this.updateById(user);
    }
}