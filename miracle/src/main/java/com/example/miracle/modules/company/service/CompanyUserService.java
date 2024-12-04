package com.example.miracle.modules.company.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.admin.entity.CompanyUser;
import com.example.miracle.modules.company.dto.CompanyLoginDTO;
import com.example.miracle.modules.company.dto.CompanyUserDTO;

public interface CompanyUserService extends IService<CompanyUser> {

    /**
     * 公司用户登录
     */
    CompanyUserDTO login(CompanyLoginDTO loginDTO);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取当前用户信息
     */
    CompanyUserDTO getCurrentUser(Long userId);
}
