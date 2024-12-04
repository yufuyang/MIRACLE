package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.company.entity.Product;
import com.example.miracle.modules.company.entity.ProductCategory;
import com.example.miracle.modules.company.entity.ProductImage;
import com.example.miracle.modules.company.entity.ProductStockLog;
import com.example.miracle.modules.company.mapper.ProductMapper;
import com.example.miracle.modules.company.service.ProductCategoryService;
import com.example.miracle.modules.company.service.ProductImageService;
import com.example.miracle.modules.company.service.ProductService;
import com.example.miracle.modules.company.service.ProductStockLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {


    private final ProductCategoryService categoryService;

    private final ProductImageService productImageService;

    private final ProductStockLogService stockLogService;


    @Override
    public Page<Product> pageProduct(Integer current, Integer size, Long merchantId,
                                     String productName, String productCode,
                                     Long categoryId, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId)
                .like(StringUtils.hasText(productName), Product::getProductName, productName)
                .eq(StringUtils.hasText(productCode), Product::getProductCode, productCode)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(status != null, Product::getStatus, status)
                .orderByDesc(Product::getSort)
                .orderByDesc(Product::getCreateTime);

        // 查询商品列表
        Page<Product> page = this.page(new Page<>(current, size), wrapper);

        // 填充分类名称
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            // 获取所有分类
            List<ProductCategory> categories = categoryService.listEnabledCategories(merchantId);
            Map<Long, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getCategoryName));

            // 设置分类名称
            page.getRecords().forEach(product ->
                    product.setCategoryName(categoryMap.get(product.getCategoryId()))
            );
        }

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(Product product, List<String> imageUrls, Integer mainImageIndex) {
        // 检查商品编码是否重复
        if (StringUtils.hasText(product.getProductCode())) {
            if (this.lambdaQuery()
                    .eq(Product::getMerchantId, product.getMerchantId())
                    .eq(Product::getProductCode, product.getProductCode())
                    .exists()) {
                throw new BusinessException("商品编码已存在");
            }
        }

        // 验证分类是否存在且启用
        ProductCategory category = categoryService.getById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }
        if (category.getStatus() != 1) {
            throw new BusinessException("商品分类已禁用");
        }

        // 设置初始值
        product.setSalesCount(0);
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getSort() == null) {
            product.setSort(0);
        }

        if (!CollectionUtils.isEmpty(imageUrls)) {
            product.setImage(imageUrls.get(mainImageIndex));
        }

        this.save(product);


        // 保存商品图片
        productImageService.saveProductImages(product.getId(), imageUrls, mainImageIndex);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Product product, List<String> imageUrls, Integer mainImageIndex) {
        // 检查商品是否存在
        Product existProduct = this.getById(product.getId());
        if (existProduct == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查商品编码是否重复
        if (StringUtils.hasText(product.getProductCode())) {
            if (this.lambdaQuery()
                    .eq(Product::getMerchantId, product.getMerchantId())
                    .eq(Product::getProductCode, product.getProductCode())
                    .ne(Product::getId, product.getId())
                    .exists()) {
                throw new BusinessException("商品编码已存在");
            }
        }

        ProductCategory category = categoryService.getById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }
        if (category.getStatus() != 1) {
            throw new BusinessException("商品分类已禁用");
        }

        if (!CollectionUtils.isEmpty(imageUrls)) {
            product.setImage(imageUrls.get(mainImageIndex));
        }

        this.updateById(product);

        // 更新商品图片
        productImageService.updateProductImages(product.getId(), imageUrls, mainImageIndex);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(status);
        this.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(Long id, Integer stock) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStock(stock);
        this.updateById(product);
    }

    @Override
    public Product getProductDetail(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 获取商品图片
        List<ProductImage> images = productImageService.getProductImages(id);
        product.setImages(images);

        // 设置主图
        images.stream()
                .filter(image -> image.getIsMain() == 1)
                .findFirst()
                .ifPresent(image -> product.setMainImage(image.getImageUrl()));

        return product;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择商品");
        }

        // 批量更新状态
        this.lambdaUpdate()
                .set(Product::getStatus, status)
                .in(Product::getId, ids)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择商品");
        }

        // 删除商品图片
        for (Long id : ids) {
            productImageService.deleteProductImages(id);
        }
        this.removeByIds(ids);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateCategory(List<Long> ids, Long categoryId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择商品");
        }
        // 验证分类是否存在且启用
        ProductCategory category = categoryService.getById(categoryId);
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }
        if (category.getStatus() != 1) {
            throw new BusinessException("商品分类已禁用");
        }

        // 批量更新分类
        this.lambdaUpdate()
                .set(Product::getCategoryId, categoryId)
                .in(Product::getId, ids)
                .update();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long id, Integer quantity) {
        // 使用乐观锁更新库存
        boolean success = this.lambdaUpdate()
                .eq(Product::getId, id)
                .ge(Product::getStock, quantity)
                .setSql("stock = stock - " + quantity)
                .update();

        if (!success) {
            throw new BusinessException("库存不足");
        }

        // 记录库存变动日志
        recordStockLog(id, quantity, "扣减库存");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseStock(Long id, Integer quantity) {
        // 增加库存
        boolean success = this.lambdaUpdate()
                .eq(Product::getId, id)
                .setSql("stock = stock + " + quantity)
                .update();

        if (!success) {
            throw new BusinessException("库存更新失败");
        }

        // 记录库存变动日志
        recordStockLog(id, quantity, "增加库存");
    }

    /**
     * 记录库存变动日志
     */
    private void recordStockLog(Long productId, Integer quantity, String type) {
        Product product = this.getById(productId);
        if (product == null) {
            return;
        }

        ProductStockLog stockLog = new ProductStockLog();
        stockLog.setProductId(productId);
        stockLog.setQuantity(quantity);
        stockLog.setType(type);
        stockLog.setBeforeStock(product.getStock() - quantity); // 变动前库存
        stockLog.setAfterStock(product.getStock()); // 变动后库存
        stockLogService.save(stockLog);
    }
}