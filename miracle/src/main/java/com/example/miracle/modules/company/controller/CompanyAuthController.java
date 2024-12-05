package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.ResultDTO;
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
    public ResultDTO<CompanyUserDTO> login(@RequestBody CompanyLoginDTO loginDTO) {
        return ResultDTO.ok(companyUserService.login(loginDTO));
    }

    @GetMapping("/info")
    public ResultDTO<CompanyUserDTO> getUserInfo(@RequestAttribute Long userId) {
        return ResultDTO.ok(companyUserService.getCurrentUser(userId));
    }

    @PutMapping("/password")
    public ResultDTO<?> updatePassword(
            @RequestAttribute Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        companyUserService.updatePassword(userId, oldPassword, newPassword);
        return ResultDTO.ok();
    }
}