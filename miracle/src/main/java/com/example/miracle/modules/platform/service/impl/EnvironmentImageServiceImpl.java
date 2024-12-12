package com.example.miracle.modules.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.platform.entity.EnvironmentImage;
import com.example.miracle.modules.platform.mapper.EnvironmentImageMapper;
import com.example.miracle.modules.platform.service.EnvironmentImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 环境照片服务实现类
 */
@Service
@RequiredArgsConstructor
public class EnvironmentImageServiceImpl extends ServiceImpl<EnvironmentImageMapper, EnvironmentImage> implements EnvironmentImageService {

    @Override
    public List<EnvironmentImage> listByOwner(Long ownerId, String ownerType) {
        return this.list(new LambdaQueryWrapper<EnvironmentImage>()
                .eq(EnvironmentImage::getOwnerId, ownerId)
                .eq(EnvironmentImage::getOwnerType, ownerType)
                .orderByAsc(EnvironmentImage::getSort));
    }


} 