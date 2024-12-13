package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.CompanyProductCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公司产品分类数据访问层
 */
@Mapper
public interface CompanyProductCategoryMapper extends BaseMapper<CompanyProductCategory> {
} 