package com.example.miracle.modules.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.miracle.modules.platform.entity.PlatformUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PlatformUserMapper extends BaseMapper<PlatformUser> {
}