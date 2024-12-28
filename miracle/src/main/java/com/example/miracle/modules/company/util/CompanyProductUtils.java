package com.example.miracle.modules.company.util;

import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.entity.CompanyProduct;

/**
 * 公司产品工具类
 */
public class CompanyProductUtils {

    /**
     * 验证产品权限
     */
    public static SingleResponse<Boolean> checkAuth(CompanyProduct product, Long companyId) {
        if (product == null) {
            return SingleResponse.buildFailure("产品不存在");
        }

        if (!product.getCompanyId().equals(companyId)) {
            return SingleResponse.buildFailure("无权操作");
        }

        return SingleResponse.buildSuccess();
    }
} 