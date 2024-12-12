package com.example.miracle.modules.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.platform.dto.cmd.SaveEnvironmentImageCmd;
import com.example.miracle.modules.platform.dto.cmd.UpdateEnvironmentImageSortCmd;
import com.example.miracle.modules.platform.entity.EnvironmentImage;

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
    
    /**
     * 保存环境照片
     *
     * @return 环境照片
     */
    EnvironmentImage saveImage(SaveEnvironmentImageCmd saveEnvironmentImageCmd);
    
    /**
     * 更新排序
     *
     * @param updateSortCmd 更新排序命令
     * @return 是否成功
     */
    boolean updateSort(UpdateEnvironmentImageSortCmd updateEnvironmentImageSortCmd);
} 