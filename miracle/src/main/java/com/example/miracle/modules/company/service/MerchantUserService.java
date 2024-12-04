package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.MerchantUser;


public interface MerchantUserService extends IService<MerchantUser> {

    /**
     * 分页查询商户用户
     */
    Page<MerchantUser> pageMerchantUser(Long merchantId, Integer current, Integer size);

    /**
     * 创建商户管理员
     */
    void createMerchantAdmin(Long merchantId, MerchantUser merchantUser);

    /**
     * 更新用户状态
     */
    void updateMerchantUserStatus(Long userId, Integer status);

    /**
     * 重置密码
     */
    void resetPassword(Long userId);
}