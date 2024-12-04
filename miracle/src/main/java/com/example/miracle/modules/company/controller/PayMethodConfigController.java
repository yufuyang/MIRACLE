package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.company.dto.PayMethodConfigDTO;
import com.example.miracle.modules.company.entity.PayMethodConfig;
import com.example.miracle.modules.company.service.PayMethodConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company/pay/method")
@RequiredArgsConstructor
public class PayMethodConfigController {

    private final PayMethodConfigService payMethodConfigService;

    /**
     * 获取可用支付方式
     */
    @GetMapping("/enabled")
    public Result<List<PayMethodConfig>> getEnabledMethods(@RequestParam Long merchantId) {
        return Result.ok(payMethodConfigService.getEnabledMethods(merchantId));
    }

    /**
     * 保存支付方式配置
     */
    @PostMapping
    public Result<?> saveConfig(@RequestBody PayMethodConfigDTO dto) {
        payMethodConfigService.saveConfig(dto);
        return Result.ok();
    }

    /**
     * 更新支付方式状态
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        payMethodConfigService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 删除支付方式配置
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteConfig(@PathVariable Long id) {
        payMethodConfigService.deleteConfig(id);
        return Result.ok();
    }
}
