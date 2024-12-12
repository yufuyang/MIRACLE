package com.example.miracle.modules.platform.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.entity.EnvironmentImage;
import com.example.miracle.modules.platform.service.EnvironmentImageService;
import com.example.miracle.modules.platform.dto.cmd.SaveEnvironmentImageCmd;
import com.example.miracle.modules.platform.dto.cmd.UpdateEnvironmentImageSortCmd;
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

    /**
     * 保存环境照片
     */
    @PostMapping
    public SingleResponse<EnvironmentImage> save(@RequestBody SaveEnvironmentImageCmd saveEnvironmentImageCmd) {
        return SingleResponse.of(environmentImageService.saveImage(saveEnvironmentImageCmd));
    }

    /**
     * 更新排序
     */
    @PutMapping("/sort")
    public SingleResponse<Boolean> updateSort(@RequestBody UpdateEnvironmentImageSortCmd updateEnvironmentImageSortCmd) {
        return SingleResponse.of(environmentImageService.updateSort(updateEnvironmentImageSortCmd));
    }

    /**
     * 删除环境照片
     */
    @DeleteMapping("/{id}")
    public SingleResponse<Boolean> removeById(@PathVariable Long id) {
        return SingleResponse.of(environmentImageService.removeById(id));
    }
} 