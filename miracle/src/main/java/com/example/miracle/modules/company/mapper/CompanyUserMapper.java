package com.example.miracle.modules.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.company.entity.CompanyUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公司用户数据访问层
 */
@Mapper
public interface CompanyUserMapper extends BaseMapper<CompanyUser> {
} 