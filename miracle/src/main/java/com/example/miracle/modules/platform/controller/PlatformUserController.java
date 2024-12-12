package com.example.miracle.modules.platform.controller;

import com.example.miracle.common.controller.BaseController;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.platform.dto.LoginDTO;
import com.example.miracle.modules.platform.dto.cmd.PlatformUserLoginCmd;
import com.example.miracle.modules.platform.dto.query.PlatformUserPageQuery;
import com.example.miracle.modules.platform.entity.PlatformUser;
import com.example.miracle.modules.platform.service.PlatformUserService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/platform/user")
@RequiredArgsConstructor
public class PlatformUserController {

    private final PlatformUserService platformUserService;

    private final HttpServletRequest request;


    @PostMapping("/login")
    SingleResponse<LoginDTO> login(@RequestBody PlatformUserLoginCmd platformUserLoginCmd) {

        return platformUserService.login(platformUserLoginCmd);
    }

    @PostMapping("/logout")
    SingleResponse logout() {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return platformUserService.logout(token);
    }

    @PostMapping("/page")
    MultiResponse<PlatformUser> pageQuery(@RequestBody PlatformUserPageQuery platformUserPageQuery) {
        return platformUserService.pageQuery(platformUserPageQuery);
    }
}
