package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyProductImage;

import java.util.List;

/**
 * 公司产品图片服务接口
 */
public interface CompanyProductImageService extends IService<CompanyProductImage> {

    /**
     * 查询产品图片列表
     */
    MultiResponse<CompanyProductImage> listByProductId(Long productId);

    /**
     * 更新排序
     */
    SingleResponse<Boolean> updateSort(Long id, Integer sort);

    /**
     * 设置主图
     */
    SingleResponse<Boolean> setMain(Long id);
} 