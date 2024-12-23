package com.example.miracle.modules.website.controller;

import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.dto.query.MerchantPageQuery;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.platform.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商户信息控制器
 */
@RestController
@RequestMapping("/website/merchant")
@RequiredArgsConstructor
public class WebsiteMerchantController {

    private final MerchantService merchantService;


    /**
     * 新增商户
     */
    @PostMapping("/register")
    public SingleResponse<Merchant> save(@RequestBody Merchant merchant) {
        merchantService.save(merchant);
        return SingleResponse.of(merchant);
    }

} 