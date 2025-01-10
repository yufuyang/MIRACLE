package com.example.miracle.modules.merchant.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.platform.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant/base")
@RequiredArgsConstructor
public class MerchantBaseController {

    private final MerchantService merchantService;

    private final BaseController baseController;

    /**
     * 新增商户
     */
    @PostMapping
    public SingleResponse<Merchant> save(@RequestBody Merchant merchant) {
        merchantService.save(merchant);
        return SingleResponse.of(merchant);
    }

    /**
     * 修改商户信息
     */
    @PutMapping
    public SingleResponse<Merchant> update(@RequestBody Merchant merchant) {
        merchantService.updateById(merchant);
        return SingleResponse.of(merchant);
    }

    /**
     * 获取商户详情
     */
    @GetMapping
    public SingleResponse<Merchant> getById() {

        Long merchantId = baseController.getMerchantId();

        return SingleResponse.of(merchantService.getById(merchantId));
    }
}
