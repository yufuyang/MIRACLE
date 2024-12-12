package com.example.miracle.modules.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户用户数据访问层
 */
@Mapper
public interface MerchantUserMapper extends BaseMapper<MerchantUser> {
} 