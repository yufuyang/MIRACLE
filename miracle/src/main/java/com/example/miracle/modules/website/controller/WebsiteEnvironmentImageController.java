package com.example.miracle.modules.website.controller;

import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.EnvironmentImage;
import com.example.miracle.modules.company.service.EnvironmentImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 环境照片控制器
 */
@RestController
@RequestMapping("/website/environment-image")
@RequiredArgsConstructor
public class WebsiteEnvironmentImageController {

    private final EnvironmentImageService environmentImageService;

    /**
     * 获取环境照片列表
     */
    @GetMapping("/list")
    public SingleResponse<List<EnvironmentImage>> list(@RequestParam Long ownerId) {
        return SingleResponse.of(environmentImageService.listByOwner(ownerId, "company"));
    }

} 