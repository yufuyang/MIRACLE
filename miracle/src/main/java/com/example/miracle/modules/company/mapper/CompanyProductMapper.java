package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.CompanyProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公司产品数据访问层
 */
@Mapper
public interface CompanyProductMapper extends BaseMapper<CompanyProduct> {
} 