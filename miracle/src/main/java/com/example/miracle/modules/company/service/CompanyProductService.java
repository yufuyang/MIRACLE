package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.company.dto.CompanyProductDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductPageQuery;
import com.example.miracle.modules.company.entity.CompanyProduct;

/**
 * 公司产品服务接口
 */
public interface CompanyProductService extends IService<CompanyProduct> {

    /**
     * 分页查询公司产品
     */
    MultiResponse<CompanyProduct> pageQuery(CompanyProductPageQuery query);
} 