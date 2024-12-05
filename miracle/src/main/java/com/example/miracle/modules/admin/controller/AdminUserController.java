package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.admin.dto.AdminUserDTO;
import com.example.miracle.modules.admin.dto.LoginDTO;
import com.example.miracle.modules.admin.dto.UpdatePasswordDTO;
import com.example.miracle.modules.admin.entity.AdminUser;
import com.example.miracle.modules.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/login")
    public ResultDTO<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        String token = adminUserService.login(loginDTO.getUsername(), loginDTO.getPassword());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return ResultDTO.ok(map);
    }

    @GetMapping("/list")
    public ResultDTO<Page<AdminUser>> list(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String realName) {
        Page<AdminUser> page = adminUserService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<AdminUser>()
                        .like(username != null, AdminUser::getUsername, username)
                        .like(realName != null, AdminUser::getRealName, realName)
                        .orderByDesc(AdminUser::getCreateTime)
        );
        return ResultDTO.ok(page);
    }

    @PostMapping("/create")
    public ResultDTO<?> create(@RequestBody AdminUserDTO adminUserDTO) {
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(adminUserDTO.getUsername());
        adminUser.setPassword(adminUserDTO.getPassword());
        adminUser.setRealName(adminUserDTO.getRealName());
        adminUser.setPhone(adminUserDTO.getPhone());
        adminUser.setEmail(adminUserDTO.getEmail());

        adminUserService.createAdmin(adminUser);
        return ResultDTO.ok();
    }

    @PutMapping("/update/{id}")
    public ResultDTO<?> update(@PathVariable Long id, @Validated @RequestBody AdminUserDTO adminUserDTO) {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(id);
        adminUser.setRealName(adminUserDTO.getRealName());
        adminUser.setPhone(adminUserDTO.getPhone());
        adminUser.setEmail(adminUserDTO.getEmail());

        adminUserService.updateAdmin(adminUser);
        return ResultDTO.ok();
    }

    @DeleteMapping("/delete/{id}")
    public ResultDTO<?> delete(@PathVariable Long id) {
        adminUserService.deleteAdmin(id);
        return ResultDTO.ok();
    }

    @PutMapping("/password/{id}")
    public ResultDTO<?> updatePassword(@PathVariable Long id, @Validated @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        adminUserService.updatePassword(id, updatePasswordDTO.getOldPassword(), updatePasswordDTO.getNewPassword());
        return ResultDTO.ok();
    }

    @PutMapping("/status/{id}/{status}")
    public ResultDTO<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(id);
        adminUser.setStatus(status);
        adminUserService.updateById(adminUser);
        return ResultDTO.ok();
    }
}