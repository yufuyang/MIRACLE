package com.example.miracle.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.CommonConstant;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.admin.entity.AdminUser;
import com.example.miracle.modules.admin.mapper.AdminUserMapper;
import com.example.miracle.modules.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final JwtUtil jwtUtil;


    @Override
    public String login(String username, String password) {
        // 查询用户
        AdminUser adminUser = this.getOne(new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, username));
        if (adminUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!Objects.equals(password, adminUser.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 验证状态
        if (CommonConstant.STATUS_DISABLE.equals(adminUser.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成token
        return jwtUtil.generateToken(username, CommonConstant.ADMIN_ROLE);
    }

    @Override
    public void createAdmin(AdminUser adminUser) {
        // 验证用户名是否存在
        if (this.count(new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, adminUser.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        adminUser.setPassword(adminUser.getPassword());
        // 设置状态
        adminUser.setStatus(CommonConstant.STATUS_NORMAL);

        this.save(adminUser);
    }

    @Override
    public void updateAdmin(AdminUser adminUser) {
        // 不允许修改用户名和密码
        adminUser.setUsername(null);
        adminUser.setPassword(null);

        this.updateById(adminUser);
    }

    @Override
    public void deleteAdmin(Long id) {
        this.removeById(id);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        AdminUser adminUser = this.getById(id);

        if (adminUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!Objects.equals(oldPassword, adminUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新密码
        adminUser.setPassword(newPassword);

        this.updateById(adminUser);
    }
}