package com.example.miracle.modules.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.platform.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户信息数据访问层
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
} 