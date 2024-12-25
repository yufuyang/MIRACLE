package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.CompanyProductDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;
import com.example.miracle.modules.website.dto.ProductDTO;

/**
 * 公司产品服务接口
 */
public interface CompanyProductService extends IService<CompanyProduct> {

    /**
     * 分页查询公司产品
     */
    MultiResponse<CompanyProduct> pageQuery(CompanyProductPageQuery query);

    /**
     * 分页查询公司产品DTO
     */
    MultiResponse<ProductDTO> pageQueryProductDTO(CompanyProductPageQuery query);

    /**
     * 保存产品（包括主图和统计信息）
     */
    CompanyProduct saveProduct(CompanyProduct companyProduct);

    /**
     * 更新产品（包括主图）
     */
    CompanyProduct updateProduct(CompanyProduct companyProduct);
} 