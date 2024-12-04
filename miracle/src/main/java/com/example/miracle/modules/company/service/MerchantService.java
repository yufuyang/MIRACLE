package com.example.miracle.modules.company.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.Merchant;
import com.example.miracle.modules.company.entity.MerchantUser;

public interface MerchantService extends IService<Merchant> {

    /**
     * 创建商户
     */
    void createMerchant(Merchant merchant);

    /**
     * 更新商户信息
     */
    void updateMerchant(Merchant merchant);

    /**
     * 审核商户
     */
    void auditMerchant(Long id, Integer status, Long auditUserId);

    /**
     * 分页查询商户列表
     */
    Page<Merchant> pageMerchant(Integer current, Integer size, Long companyId, String merchantName,
                                String merchantCode, Integer status);

    /**
     * 获取商户详情
     */
    Merchant getMerchantDetail(Long id);

    /**
     * 生成商户编码
     */
    String generateMerchantCode(Long companyId);

    /**
     * 创建商户管理员
     */
    void createMerchantAdmin(Long merchantId, MerchantUser merchantUser);

    /**
     * 获取商户用户列表
     */
    Page<MerchantUser> pageMerchantUser(Long merchantId, Integer current, Integer size);

    /**
     * 更新商户用户状态
     */
    void updateMerchantUserStatus(Long userId, Integer status);
}