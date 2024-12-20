package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.EnvironmentImage;

import java.util.List;

/**
 * 环境照片服务接口
 */
public interface EnvironmentImageService extends IService<EnvironmentImage> {
    
    /**
     * 获取环境照片列表
     *
     * @param ownerId 所属者ID
     * @param ownerType 所属者类型
     * @return 环境照片列表
     */
    List<EnvironmentImage> listByOwner(Long ownerId, String ownerType);

} 