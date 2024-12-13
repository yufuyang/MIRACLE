package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.mapper.CompanyProductImageMapper;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.util.CompanyProductUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公司产品图片服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProductImageServiceImpl extends ServiceImpl<CompanyProductImageMapper, CompanyProductImage> implements CompanyProductImageService {


    @Override
    public MultiResponse<CompanyProductImage> listByProductId(Long productId) {
        List<CompanyProductImage> list = this.list(new LambdaQueryWrapper<CompanyProductImage>()
                .eq(CompanyProductImage::getProductId, productId)
                .orderByDesc(CompanyProductImage::getIsMain)
                .orderByAsc(CompanyProductImage::getSort));

        return MultiResponse.of(list);
    }

    @Override
    public SingleResponse<Boolean> updateSort(Long id, Integer sort) {
        CompanyProductImage image = new CompanyProductImage();
        image.setId(id);
        image.setSort(sort);
        return SingleResponse.of(this.updateById(image));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse<Boolean> setMain(Long id) {
        // 获取图片信息
        CompanyProductImage image = this.getById(id);
        if (image == null) {
            return SingleResponse.buildFailure("图片不存在");
        }

        // 取消原主图
        this.update(new LambdaUpdateWrapper<CompanyProductImage>()
                .eq(CompanyProductImage::getProductId, image.getProductId())
                .eq(CompanyProductImage::getIsMain, 1)
                .set(CompanyProductImage::getIsMain, 0));

        // 设置新主图
        image.setIsMain(1);
        this.updateById(image);

        return SingleResponse.buildSuccess();
    }

} 