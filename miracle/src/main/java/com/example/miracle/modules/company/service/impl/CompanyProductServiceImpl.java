package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.entity.CompanyProductImage;
import com.example.miracle.modules.company.entity.CompanyProductStats;
import com.example.miracle.modules.company.mapper.CompanyProductMapper;
import com.example.miracle.modules.company.service.CompanyProductService;
import com.example.miracle.modules.company.service.CompanyProductImageService;
import com.example.miracle.modules.company.service.CompanyProductStatsService;
import com.example.miracle.modules.website.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 公司产品服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProductServiceImpl extends ServiceImpl<CompanyProductMapper, CompanyProduct> implements CompanyProductService {

    private final CompanyProductImageService productImageService;

    @Override
    public MultiResponse<CompanyProduct> pageQuery(CompanyProductPageQuery companyProductPageQuery) {
        // 普通查询
        LambdaQueryWrapper<CompanyProduct> wrapper = new LambdaQueryWrapper<CompanyProduct>()
                .eq(Objects.nonNull(companyProductPageQuery.getCompanyId()), CompanyProduct::getCompanyId, companyProductPageQuery.getCompanyId())
                .like(StringUtils.isNotBlank(companyProductPageQuery.getProductName()), CompanyProduct::getProductName, companyProductPageQuery.getProductName())
                .like(StringUtils.isNotBlank(companyProductPageQuery.getProductCode()), CompanyProduct::getProductCode, companyProductPageQuery.getProductCode())
                .eq(companyProductPageQuery.getStatus() != null, CompanyProduct::getStatus, companyProductPageQuery.getStatus())
                .orderByDesc(CompanyProduct::getCreateTime);

        Page<CompanyProduct> page = this.page(new Page<>(companyProductPageQuery.getPageNum(), companyProductPageQuery.getPageSize()), wrapper);

        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }

    @Override
    public MultiResponse<ProductDTO> pageQueryProductDTO(CompanyProductPageQuery companyProductPageQuery) {
        // 使用 Mapper 进行分页查询
        Page<ProductDTO> page = this.baseMapper.selectProductDTOPage(new Page<>(companyProductPageQuery.getPageNum(), companyProductPageQuery.getPageSize()), companyProductPageQuery);

        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyProduct saveProduct(CompanyProduct companyProduct) {
        // 保存产品
        this.save(companyProduct);

        // 保存主图
        CompanyProductImage companyProductImage = new CompanyProductImage();
        companyProductImage.setProductId(companyProduct.getId());
        companyProductImage.setImageUrl(companyProduct.getImageUrl());
        companyProductImage.setSort(1);
        companyProductImage.setIsMain(1);
        productImageService.save(companyProductImage);

//        // 初始化统计数据
//        CompanyProductStats companyProductStats = new CompanyProductStats();
//        companyProductStats.setProductId(companyProduct.getId());
//        companyProductStats.setCompanyId(companyProduct.getCompanyId());
//        companyProductStats.setIntentionCount(0);
//        companyProductStats.setViewCount(0);
//        companyProductStatsService.save(companyProductStats);

        return companyProduct;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyProduct updateProduct(CompanyProduct companyProduct) {
        // 更新产品信息
        this.updateById(companyProduct);

        // 更新主图
        productImageService.update(
                new LambdaUpdateWrapper<CompanyProductImage>()
                        .eq(CompanyProductImage::getProductId, companyProduct.getId())
                        .eq(CompanyProductImage::getIsMain, 1)
                        .set(CompanyProductImage::getImageUrl, companyProduct.getImageUrl())
        );

        return companyProduct;
    }
} 