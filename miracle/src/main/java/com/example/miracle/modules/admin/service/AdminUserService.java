package com.example.miracle.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.admin.entity.AdminUser;

public interface AdminUserService extends IService<AdminUser> {

    /**
     * 管理员登录
     */
    String login(String username, String password);

    /**
     * 创建管理员
     */
    void createAdmin(AdminUser adminUser);

    /**
     * 更新管理员信息
     */
    void updateAdmin(AdminUser adminUser);

    /**
     * 删除管理员
     */
    void deleteAdmin(Long id);

    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);
}
