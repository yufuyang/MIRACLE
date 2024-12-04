package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.company.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品
     */
    Page<Product> pageProduct(Integer current, Integer size, Long merchantId,
                              String productName, String productCode,
                              Long categoryId, Integer status);

    /**
     * 创建商品
     */
    void createProduct(Product product, List<String> imageUrls, Integer mainImageIndex);

    /**
     * 更新商品
     */
    void updateProduct(Product product, List<String> imageUrls, Integer mainImageIndex);

    /**
     * 更新商品状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 更新商品库存
     */
    void updateStock(Long id, Integer stock);

    /**
     * 获取商品详情
     */
    Product getProductDetail(Long id);

    /**
     * 批量更新商品状态
     */
    void batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 批量删除商品
     */
    void batchDelete(List<Long> ids);

    /**
     * 批量更新商品分类
     */
    void batchUpdateCategory(List<Long> ids, Long categoryId);

    /**
     * 扣减库存
     */
    void deductStock(Long id, Integer quantity);

    /**
     * 增加库存
     */
    void increaseStock(Long id, Integer quantity);
}