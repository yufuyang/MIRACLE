package com.example.miracle.modules.company.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.entity.Merchant;
import com.example.miracle.modules.company.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/merchant")
@RequiredArgsConstructor
public class CompanyMerchantController {

    private final MerchantService merchantService;

    @GetMapping("/page")
    public ResultDTO<Page<Merchant>> page(
            @RequestAttribute Long companyId,  // 从token中获取公司ID
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String merchantName,
            @RequestParam(required = false) String merchantCode,
            @RequestParam(required = false) Integer status) {
        return ResultDTO.ok(merchantService.pageMerchant(current, size, companyId, merchantName, merchantCode, status));
    }

    @GetMapping("/{id}")
    public ResultDTO<Merchant> detail(
            @RequestAttribute Long companyId,
            @PathVariable Long id) {
        // 验证商户是否属于当前公司
        Merchant merchant = merchantService.getMerchantDetail(id);
        if (!merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权访问该商户");
        }
        return ResultDTO.ok(merchant);
    }

    @PostMapping
    public ResultDTO<?> create(
            @RequestAttribute Long companyId,
            @RequestBody Merchant merchant) {
        merchant.setCompanyId(companyId);  // 设置公司ID
        merchantService.createMerchant(merchant);
        return ResultDTO.ok();
    }

    @PutMapping
    public ResultDTO<?> update(
            @RequestAttribute Long companyId,
            @RequestBody Merchant merchant) {
        // 验证商户是否属于当前公司
        Merchant existMerchant = merchantService.getById(merchant.getId());
        if (!existMerchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权修改该商户");
        }
        merchantService.updateMerchant(merchant);
        return ResultDTO.ok();
    }

    @PutMapping("/audit/{id}")
    public ResultDTO<?> audit(
            @RequestAttribute Long companyId,
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestAttribute Long userId) {  // 从token中获取用户ID
        // 验证商户是否属于当前公司
        Merchant merchant = merchantService.getById(id);
        if (!merchant.getCompanyId().equals(companyId)) {
            return ResultDTO.error("无权审核该商户");
        }
        merchantService.auditMerchant(id, status, userId);
        return ResultDTO.ok();
    }
}
