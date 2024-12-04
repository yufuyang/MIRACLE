package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.modules.company.entity.ProductImage;
import com.example.miracle.modules.company.mapper.ProductImageMapper;
import com.example.miracle.modules.company.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {

    @Override
    public List<ProductImage> getProductImages(Long productId) {
        return this.lambdaQuery()
                .eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSort)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProductImages(Long productId, List<String> imageUrls, Integer mainImageIndex) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<ProductImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setImageUrl(imageUrls.get(i));
            image.setSort(i);
            image.setIsMain(i == mainImageIndex ? 1 : 0);
            images.add(image);
        }

        this.saveBatch(images);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductImages(Long productId, List<String> imageUrls, Integer mainImageIndex) {
        // 删除原有图片
        this.lambdaUpdate()
                .eq(ProductImage::getProductId, productId)
                .remove();

        // 保存新图片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            this.saveProductImages(productId, imageUrls, mainImageIndex);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductImages(Long productId) {
        this.lambdaUpdate()
                .eq(ProductImage::getProductId, productId)
                .remove();
    }
}