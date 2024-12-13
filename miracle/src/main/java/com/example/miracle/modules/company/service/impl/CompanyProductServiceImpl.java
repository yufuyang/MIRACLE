package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.company.mapper.CompanyProductMapper;
import com.example.miracle.modules.company.service.CompanyProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 公司产品服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyProductServiceImpl extends ServiceImpl<CompanyProductMapper, CompanyProduct> implements CompanyProductService {

    @Override
    public MultiResponse<CompanyProduct> pageQuery(CompanyProductPageQuery companyProductPageQuery) {
        LambdaQueryWrapper<CompanyProduct> wrapper = new LambdaQueryWrapper<CompanyProduct>()
                .eq(CompanyProduct::getCompanyId, companyProductPageQuery.getCompanyId())
                .like(StringUtils.isNotBlank(companyProductPageQuery.getProductName()), CompanyProduct::getProductName, companyProductPageQuery.getProductName())
                .like(StringUtils.isNotBlank(companyProductPageQuery.getProductCode()), CompanyProduct::getProductCode, companyProductPageQuery.getProductCode())
                .eq(StringUtils.isNotBlank(companyProductPageQuery.getCategory()), CompanyProduct::getCategoryId, companyProductPageQuery.getCategory())
                .eq(companyProductPageQuery.getStatus() != null, CompanyProduct::getStatus, companyProductPageQuery.getStatus())
                .orderByDesc(CompanyProduct::getCreateTime);

        Page<CompanyProduct> page = this.page(new Page<>(companyProductPageQuery.getPageNum(), companyProductPageQuery.getPageSize()), wrapper);

        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }
} 