package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.ProductImage;

import java.util.List;

public interface ProductImageService extends IService<ProductImage> {

    /**
     * 获取商品图片列表
     */
    List<ProductImage> getProductImages(Long productId);

    /**
     * 保存商品图片
     */
    void saveProductImages(Long productId, List<String> imageUrls, Integer mainImageIndex);

    /**
     * 更新商品图片
     */
    void updateProductImages(Long productId, List<String> imageUrls, Integer mainImageIndex);

    /**
     * 删除商品图片
     */
    void deleteProductImages(Long productId);
}