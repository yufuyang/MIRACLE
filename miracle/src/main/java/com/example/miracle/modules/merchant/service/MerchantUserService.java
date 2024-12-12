package com.example.miracle.modules.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.merchant.dto.MerchantUserLoginDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantUserLoginCmd;
import com.example.miracle.modules.merchant.entity.MerchantUser;

/**
 * 商户用户服务接口
 */
public interface MerchantUserService extends IService<MerchantUser> {

    /**
     * 用户登录
     */
    SingleResponse<MerchantUserLoginDTO> login(MerchantUserLoginCmd merchantUserLoginCmd);

    /**
     * 用户登出
     */
    SingleResponse<Void> logout(String token);

} 