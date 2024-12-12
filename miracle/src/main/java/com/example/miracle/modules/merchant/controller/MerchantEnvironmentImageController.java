package com.example.miracle.modules.merchant.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.entity.EnvironmentImage;
import com.example.miracle.modules.platform.service.EnvironmentImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 环境照片控制器
 */
@RestController
@RequestMapping("/platform/environment-image")
@RequiredArgsConstructor
public class MerchantEnvironmentImageController {

    private final EnvironmentImageService environmentImageService;

    private final BaseController baseController;

    /**
     * 获取环境照片列表
     */
    @GetMapping("/list")
    public SingleResponse<List<EnvironmentImage>> list() {

        Long merchantId = baseController.getMerchantId();
        return SingleResponse.of(environmentImageService.listByOwner(merchantId, "merchant"));
    }

    /**
     * 保存环境照片
     */
    @PostMapping
    public SingleResponse<EnvironmentImage> save(@RequestBody EnvironmentImage environmentImage) {

        Long merchantId = baseController.getMerchantId();

        environmentImage.setOwnerId(merchantId);
        environmentImage.setOwnerType("merchant");
        environmentImageService.save(environmentImage);

        return SingleResponse.buildSuccess();
    }

    /**
     * 更新排序
     */
    @PutMapping("/sort")
    public SingleResponse<Boolean> updateSort(@RequestBody EnvironmentImage environmentImage) {
        environmentImageService.updateById(environmentImage);
        return SingleResponse.buildSuccess();
    }

    /**
     * 删除环境照片
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {
        environmentImageService.removeById(id);
        return SingleResponse.buildSuccess();
    }
} 