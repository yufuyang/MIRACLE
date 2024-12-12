package com.example.miracle.modules.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.dto.LoginDTO;
import com.example.miracle.modules.platform.dto.cmd.PlatformUserLoginCmd;
import com.example.miracle.modules.platform.dto.query.PlatformUserPageQuery;
import com.example.miracle.modules.platform.entity.PlatformUser;

public interface PlatformUserService extends IService<PlatformUser> {
    SingleResponse<LoginDTO> login(PlatformUserLoginCmd platformUserLoginCmd);

    SingleResponse logout(String token);

    /**
     * 分页查询平台用户
     */
    MultiResponse<PlatformUser> pageQuery(PlatformUserPageQuery platformUserPageQuery);
}
