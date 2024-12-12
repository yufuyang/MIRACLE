package com.example.miracle.modules.platform.controller;

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
public class EnvironmentImageController {

    private final EnvironmentImageService environmentImageService;

    /**
     * 获取环境照片列表
     */
    @GetMapping("/list")
    public SingleResponse<List<EnvironmentImage>> list(@RequestParam Long ownerId, @RequestParam String ownerType) {
        return SingleResponse.of(environmentImageService.listByOwner(ownerId, ownerType));
    }

} 