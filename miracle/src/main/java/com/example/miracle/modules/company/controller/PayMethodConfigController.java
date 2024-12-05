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
     * 更新支付方式状态
     */
    @PutMapping("/{id}/status")
    public ResultDTO<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        payMethodConfigService.updateStatus(id, status);
        return ResultDTO.ok();
    }


}
