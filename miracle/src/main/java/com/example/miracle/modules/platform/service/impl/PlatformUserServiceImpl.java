package com.example.miracle.modules.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.platform.dto.LoginDTO;
import com.example.miracle.modules.platform.dto.cmd.PlatformUserLoginCmd;
import com.example.miracle.modules.platform.dto.query.PlatformUserPageQuery;
import com.example.miracle.modules.platform.entity.PlatformUser;
import com.example.miracle.modules.platform.mapper.PlatformUserMapper;
import com.example.miracle.modules.platform.service.PlatformUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

@Service
@AllArgsConstructor
public class PlatformUserServiceImpl extends ServiceImpl<PlatformUserMapper, PlatformUser> implements PlatformUserService {


    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    @Override
    public SingleResponse<LoginDTO> login(PlatformUserLoginCmd platformUserLoginCmd) {

        PlatformUser user = this.getOne(new LambdaQueryWrapper<PlatformUser>().eq(PlatformUser::getUsername, platformUserLoginCmd.getUsername()));

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!Objects.equals(platformUserLoginCmd.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());

        this.updateById(user);

        String token = jwtUtil.generateToken(user);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(user.getUsername());
        loginDTO.setToken(token);

        return SingleResponse.of(loginDTO);

    }

    @Override
    public SingleResponse logout(String token) {

        jwtUtil.invalidateToken(token);

        return SingleResponse.buildSuccess();
    }

    @Override
    public MultiResponse<PlatformUser> pageQuery(PlatformUserPageQuery platformUserPageQuery) {

        // 构建查询条件
        LambdaQueryWrapper<PlatformUser> wrapper = new LambdaQueryWrapper<PlatformUser>()
                .like(StringUtils.isNotBlank(platformUserPageQuery.getUsername()), PlatformUser::getUsername, platformUserPageQuery.getUsername())
                .like(StringUtils.isNotBlank(platformUserPageQuery.getRealName()), PlatformUser::getRealName, platformUserPageQuery.getRealName())
                .eq(platformUserPageQuery.getStatus() != null, PlatformUser::getStatus, platformUserPageQuery.getStatus())
                .orderByDesc(PlatformUser::getCreateTime);

        // 执行分页查询
        Page<PlatformUser> page = this.page(new Page<>(platformUserPageQuery.getPageNum(), platformUserPageQuery.getPageSize()), wrapper);

        // 返回结果
        return MultiResponse.of(page.getRecords(), (int) page.getTotal());
    }
}