package com.example.miracle.modules.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.CommonConstant;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.merchant.dto.MerchantUserLoginDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantUserLoginCmd;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.merchant.mapper.MerchantUserMapper;
import com.example.miracle.modules.merchant.service.MerchantUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 商户用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class MerchantUserServiceImpl extends ServiceImpl<MerchantUserMapper, MerchantUser> implements MerchantUserService {

    private final JwtUtil jwtUtil;

    @Override
    public SingleResponse<MerchantUserLoginDTO> login(MerchantUserLoginCmd merchantUserLoginCmd) {
        // 查询用户
        MerchantUser user = this.getOne(new LambdaQueryWrapper<MerchantUser>().eq(MerchantUser::getUsername, merchantUserLoginCmd.getUsername()));

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!Objects.equals(merchantUserLoginCmd.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 验证状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成token
        String token = jwtUtil.generateToken(user);

        // 构建返回对象
        MerchantUserLoginDTO loginDTO = new MerchantUserLoginDTO();
        loginDTO.setUsername(user.getUsername());
        loginDTO.setToken(token);
        loginDTO.setRole(CommonConstant.MERCHANT_ROLE);
        return SingleResponse.of(loginDTO);
    }

    @Override
    public SingleResponse<Void> logout(String token) {
        jwtUtil.invalidateToken(token);
        return SingleResponse.buildSuccess();
    }

} 