package com.example.miracle.modules.merchant.controller;

import com.example.miracle.common.constant.CommonConstant;
import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.merchant.dto.MerchantUserLoginDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantUserLoginCmd;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.merchant.service.MerchantUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 商户用户控制器
 */
@RestController
@RequestMapping("/merchant/user")
@RequiredArgsConstructor
public class MerchantUserController {

    private final MerchantUserService merchantUserService;
    private final HttpServletRequest request;
    private final BaseController baseController;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public SingleResponse<MerchantUserLoginDTO> login(@RequestBody MerchantUserLoginCmd merchantUserLoginCmd) {
        return merchantUserService.login(merchantUserLoginCmd);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public SingleResponse<Void> logout() {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return merchantUserService.logout(token);
    }

    @GetMapping
    public SingleResponse<MerchantUser> getById() {
        Long id = baseController.getUserId();
        MerchantUser merchantUser = merchantUserService.getById(id);
        merchantUser.setRole(CommonConstant.MERCHANT_ROLE);
        return SingleResponse.of(merchantUser);
    }


} 