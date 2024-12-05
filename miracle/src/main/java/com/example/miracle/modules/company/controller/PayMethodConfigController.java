package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.ResultDTO;
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
    public ResultDTO<List<PayMethodConfig>> getEnabledMethods(@RequestParam Long merchantId) {
        return ResultDTO.ok(payMethodConfigService.getEnabledMethods(merchantId));
    }

    /**
     * 保存支付方式配置
     */
    @PostMapping
    public ResultDTO<?> saveConfig(@RequestBody PayMethodConfigDTO dto) {
        payMethodConfigService.saveConfig(dto);
        return ResultDTO.ok();
    }

    /**
     * 更新支付方式状态
     */
    @PutMapping("/{id}/status")
    public ResultDTO<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        payMethodConfigService.updateStatus(id, status);
        return ResultDTO.ok();
    }

    /**
     * 删除支付方式配置
     */
    @DeleteMapping("/{id}")
    public ResultDTO<?> deleteConfig(@PathVariable Long id) {
        payMethodConfigService.deleteConfig(id);
        return ResultDTO.ok();
    }
}
