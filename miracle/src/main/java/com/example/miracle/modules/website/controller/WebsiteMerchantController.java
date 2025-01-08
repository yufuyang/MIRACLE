package com.example.miracle.modules.website.controller;

import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.merchant.service.MerchantUserService;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.platform.service.MerchantService;
import com.example.miracle.modules.website.dto.cmd.MerchantRegisterCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 商户信息控制器
 */
@RestController
@RequestMapping("/website/merchant")
@RequiredArgsConstructor
public class WebsiteMerchantController {

    private final MerchantService merchantService;

    private final MerchantUserService merchantUserService;


    /**
     * 新增商户
     */
    @PostMapping("/register")
    public SingleResponse<Merchant> save(@RequestBody MerchantRegisterCmd merchantRegisterCmd) {

        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(merchantRegisterCmd, merchant);

        merchantService.save(merchant);

        MerchantUser merchantUser = new MerchantUser();
        BeanUtils.copyProperties(merchantRegisterCmd, merchantUser);
        merchantUser.setMerchantId(merchant.getId());

        merchantUserService.save(merchantUser);

        return SingleResponse.of(merchant);
    }

    /**
     * 获取商户详情
     */
    @GetMapping("/{id}")
    public SingleResponse<Merchant> getById(@PathVariable Long id) {
        return SingleResponse.of(merchantService.getById(id));
    }


} 