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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final StringRedisTemplate redisTemplate;
    private final ProductStockLogService stockLogService;
    private final ProductCategoryService categoryService;
    private final ProductImageService imageService;

    private static final String STOCK_KEY = "product:stock:";
    private static final String STOCK_LOCK_KEY = "product:stock:lock:";
    private static final long STOCK_CACHE_SECONDS = 3600;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(Product product, List<String> imageUrls, Integer mainImageIndex) {
        // 1. 校验分类
        validateCategory(product.getCategoryId(), product.getCompanyId());

        // 2. 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getSort() == null) {
            product.setSort(0);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }

        // 3. 保存商品
        this.save(product);

        // 4. 保存商品图片
        if (!CollectionUtils.isEmpty(imageUrls)) {
            imageService.saveProductImages(product.getId(), imageUrls, mainImageIndex);
        }

        // 5. 初始化库存缓存
        initStockCache(product.getId(), product.getStock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long id, Integer quantity) {
        String lockKey = STOCK_LOCK_KEY + id;
        if (!tryLock(lockKey, 30)) {
            throw new BusinessException("商品库存处理中，请稍后重试");
        }

        try {
            // 1. 检查缓存中的库存
            Integer stock = getStockFromCache(id);
            if (stock != null && stock < quantity) {
                throw new BusinessException("库存不足");
            }

            // 2. 更新数据库库存
            boolean success = this.lambdaUpdate()
                    .eq(Product::getId, id)
                    .ge(Product::getStock, quantity)
                    .setSql("stock = stock - " + quantity)
                    .update();

            if (!success) {
                // 验证是否真的库存不足
                Product product = this.getById(id);
                if (product.getStock() < quantity) {
                    throw new BusinessException("库存不足");
                }
                throw new BusinessException("库存扣减失败，请重试");
            }

            // 3. 更新缓存
            updateStockCache(id, -quantity);

            // 4. 记录库存日志
            recordStockLog(id, quantity, "扣减库存");

            // 5. 检查库存预警
            checkStockWarning(id);

        } finally {
            unlock(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseStock(Long id, Integer quantity) {
        String lockKey = STOCK_LOCK_KEY + id;
        if (!tryLock(lockKey, 30)) {
            throw new BusinessException("商品库存处理中，请稍后重试");
        }

        try {
            // 1. 更新数据库库存
            this.lambdaUpdate()
                    .eq(Product::getId, id)
                    .setSql("stock = stock + " + quantity)
                    .update();

            // 2. 更新缓存
            updateStockCache(id, quantity);

            // 3. 记录库存日志
            recordStockLog(id, quantity, "增加库存");

        } finally {
            unlock(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseSales(Long productId, Integer quantity) {
        // 1. 查询商品
        Product product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 2. 更新销量
        boolean success = this.lambdaUpdate()
                .eq(Product::getId, productId)
                .setSql("sales = sales + " + quantity)
                .update();

        if (!success) {
            throw new BusinessException("更新商品销量失败");
        }

        // 3. 记录日志
        log.info("商品[{}]销量增加{}件", productId, quantity);
    }

    private void validateCategory(Long categoryId, Long companyId) {
        ProductCategory category = categoryService.getById(categoryId);
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }
        if (!category.getCompanyId().equals(companyId)) {
            throw new BusinessException("商品分类不属于当前商户");
        }
        if (category.getStatus() != 1) {
            throw new BusinessException("商品分类已禁用");
        }
    }

    private void initStockCache(Long productId, Integer stock) {
        redisTemplate.opsForValue().set(STOCK_KEY + productId, 
            String.valueOf(stock), STOCK_CACHE_SECONDS, TimeUnit.SECONDS);
    }

    private Integer getStockFromCache(Long productId) {
        String stock = redisTemplate.opsForValue().get(STOCK_KEY + productId);
        return stock != null ? Integer.parseInt(stock) : null;
    }

    private void updateStockCache(Long productId, Integer delta) {
        redisTemplate.opsForValue().increment(STOCK_KEY + productId, delta);
    }

    private void recordStockLog(Long productId, Integer quantity, String type) {
        ProductStockLog log = new ProductStockLog();
        log.setProductId(productId);
        log.setQuantity(quantity);
        log.setType(type);
        stockLogService.save(log);
    }

    private void checkStockWarning(Long productId) {
        Integer stock = getStockFromCache(productId);
        if (stock != null && stock < 10) {
            log.warn("商品[{}]库存预警，当前库存：{}", productId, stock);
        }
    }

    private boolean tryLock(String key, long seconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", seconds, TimeUnit.SECONDS));
    }

    private void unlock(String key) {
        redisTemplate.delete(key);
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
    public Page<Product> pageProduct(Integer current, Integer size, Long companyId,
                                   String productName, String productCode,
                                   Long categoryId, Integer status) {
        return this.page(new Page<>(current, size),
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getCompanyId, companyId)
                        .like(StringUtils.hasText(productName), Product::getProductName, productName)
                        .eq(StringUtils.hasText(productCode), Product::getProductCode, productCode)
                        .eq(categoryId != null, Product::getCategoryId, categoryId)
                        .eq(status != null, Product::getStatus, status)
                        .orderByDesc(Product::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Product product, List<String> imageUrls, Integer mainImageIndex) {
        validateCategory(product.getCategoryId(), product.getCompanyId());
        this.updateById(product);
        
        if (!CollectionUtils.isEmpty(imageUrls)) {
            imageService.updateProductImages(product.getId(), imageUrls, mainImageIndex);
        }
    }

    @Override
    public void updateStock(Long id, Integer stock) {
        this.lambdaUpdate()
                .set(Product::getStock, stock)
                .eq(Product::getId, id)
                .update();
        initStockCache(id, stock);
    }

    @Override
    public Product getProductDetail(Long id) {
        Product product = this.getById(id);
        if (product != null) {
            List<ProductImage> images = imageService.getProductImages(id);
            product.setImages(images);
        }
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        this.lambdaUpdate()
                .set(Product::getStatus, status)
                .in(Product::getId, ids)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        this.removeByIds(ids);
        // 删除商品图片
        ids.forEach(imageService::deleteProductImages);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateCategory(List<Long> ids, Long categoryId) {
        this.lambdaUpdate()
                .set(Product::getCategoryId, categoryId)
                .in(Product::getId, ids)
                .update();
    }
}

