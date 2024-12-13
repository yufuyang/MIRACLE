package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.CompanyProductCategoryTreeDTO;
import com.example.miracle.modules.company.dto.query.CompanyProductCategoryQuery;
import com.example.miracle.modules.company.entity.CompanyProductCategory;

import java.util.List;

/**
 * 公司产品分类服务接口
 */
public interface CompanyProductCategoryService extends IService<CompanyProductCategory> {

    /**
     * 查询分类列表
     */
    MultiResponse<CompanyProductCategory> list(CompanyProductCategoryQuery query);

    /**
     * 更新排序
     */
    SingleResponse<Boolean> updateSort(Long id, Integer sort);

    /**
     * 获取分类树形结构
     */
    MultiResponse<CompanyProductCategoryTreeDTO> tree(Long companyId);

} 