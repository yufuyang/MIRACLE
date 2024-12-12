package com.example.miracle.modules.platform.controller;

import com.example.miracle.common.controller.BaseController;
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
@RequestMapping("/platform/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;


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
    @GetMapping("/{id}")
    public SingleResponse<Merchant> getById(@PathVariable Long id) {
        return SingleResponse.of(merchantService.getById(id));
    }

    /**
     * 删除商户
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {
        return SingleResponse.of(merchantService.removeById(id));
    }

    /**
     * 分页查询商户列表
     */
    @PostMapping("/page")
    public MultiResponse<Merchant> pageQuery(@RequestBody MerchantPageQuery merchantPageQuery) {
        return merchantService.pageQuery(merchantPageQuery);
    }
} 