package com.example.miracle.common.interceptor;

import com.example.miracle.common.constant.CommonConstant;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@RequiredArgsConstructor
public class PlatformAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取token
        String token = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (token == null) {
            throw new BusinessException("未登录");
        }

        // 如果token以Bearer 开头，去掉Bearer 
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            throw new BusinessException("token无效");
        }

        // 验证角色
        String role = claims.get("role", String.class);
        if (!CommonConstant.PLATFORM_ROLE.equals(role)) {
            throw new BusinessException("无权限");
        }

        return true;
    }
}