package com.example.miracle.modules.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.modules.platform.dto.query.MerchantPageQuery;
import com.example.miracle.modules.platform.entity.Merchant;

/**
 * 商户信息服务接口
 */
public interface MerchantService extends IService<Merchant> {

    /**
     * 分页查询商户列表
     *
     * @param merchantPageQuery 查询条件
     * @return 分页结果
     */
    MultiResponse<Merchant> pageQuery(MerchantPageQuery merchantPageQuery);
} 