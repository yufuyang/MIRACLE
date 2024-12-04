package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.company.dto.CompanyLoginDTO;
import com.example.miracle.modules.company.dto.CompanyUserDTO;
import com.example.miracle.modules.company.service.CompanyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company/auth")
@RequiredArgsConstructor
public class CompanyAuthController {

    private final CompanyUserService companyUserService;

    @PostMapping("/login")
    public Result<CompanyUserDTO> login(@RequestBody CompanyLoginDTO loginDTO) {
        return Result.ok(companyUserService.login(loginDTO));
    }

    @GetMapping("/info")
    public Result<CompanyUserDTO> getUserInfo(@RequestAttribute Long userId) {
        return Result.ok(companyUserService.getCurrentUser(userId));
    }

    @PutMapping("/password")
    public Result<?> updatePassword(
            @RequestAttribute Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        companyUserService.updatePassword(userId, oldPassword, newPassword);
        return Result.ok();
    }
}