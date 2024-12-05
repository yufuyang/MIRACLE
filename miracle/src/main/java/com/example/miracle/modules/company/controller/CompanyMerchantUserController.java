package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.entity.Merchant;
import com.example.miracle.modules.company.entity.MerchantUser;
import com.example.miracle.modules.company.service.MerchantService;
import com.example.miracle.modules.company.service.MerchantUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/merchant/user")
@RequiredArgsConstructor
public class CompanyMerchantUserController {

    private final MerchantService merchantService;
    private final MerchantUserService merchantUserService;

    @GetMapping("/page/{merchantId}")
    public ResultDTO<Page<MerchantUser>> page(
            @RequestAttribute Long companyId,
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        // 验证商户是否属于当前公司
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null || !merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权访问该商户");
        }
        return ResultDTO.ok(merchantUserService.pageMerchantUser(merchantId, current, size));
    }

    @PostMapping("/{merchantId}")
    public ResultDTO<?> create(
            @RequestAttribute Long companyId,
            @PathVariable Long merchantId,
            @RequestBody MerchantUser merchantUser) {
        // 验证商户是否属于当前公司
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null || !merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权操作该商户");
        }
        merchantUser.setMerchantId(merchantId);
        merchantUserService.createMerchantAdmin(merchantId, merchantUser);
        return ResultDTO.ok();
    }

    @PutMapping("/status/{userId}")
    public ResultDTO<?> updateStatus(
            @RequestAttribute Long companyId,
            @PathVariable Long userId,
            @RequestParam Integer status) {
        // 验证用户所属商户是否属于当前公司
        MerchantUser merchantUser = merchantUserService.getById(userId);
        if (merchantUser == null) {
            return ResultDTO.error("用户不存在");
        }
        Merchant merchant = merchantService.getById(merchantUser.getMerchantId());
        if (!merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权操作该用户");
        }
        merchantUserService.updateMerchantUserStatus(userId, status);
        return ResultDTO.ok();
    }

    @DeleteMapping("/{userId}")
    public ResultDTO<?> delete(
            @RequestAttribute Long companyId,
            @PathVariable Long userId) {
        // 验证用户所属商户是否属于当前公司
        MerchantUser merchantUser = merchantUserService.getById(userId);
        if (merchantUser == null) {
            return ResultDTO.error("用户不存在");
        }
        Merchant merchant = merchantService.getById(merchantUser.getMerchantId());
        if (!merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权删除该用户");
        }
        merchantUserService.removeById(userId);
        return ResultDTO.ok();
    }

    @PutMapping("/reset-password/{userId}")
    public ResultDTO<?> resetPassword(
            @RequestAttribute Long companyId,
            @PathVariable Long userId) {
        // 验证用户所属商户是否属于当前公司
        MerchantUser merchantUser = merchantUserService.getById(userId);
        if (merchantUser == null) {
            return ResultDTO.error("用户不存在");
        }
        Merchant merchant = merchantService.getById(merchantUser.getMerchantId());
        if (!merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权操作该用户");
        }
        merchantUserService.resetPassword(userId);
        return ResultDTO.ok();
    }
}