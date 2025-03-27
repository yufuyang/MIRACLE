package com.example.miracle.modules.merchant.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.constant.CommonConstant;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.common.utils.JwtUtil;
import com.example.miracle.modules.merchant.dto.MerchantUserLoginDTO;
import com.example.miracle.modules.merchant.dto.WxPhoneLoginDTO;
import com.example.miracle.modules.merchant.dto.cmd.MerchantUserLoginCmd;
import com.example.miracle.modules.merchant.entity.MerchantUser;
import com.example.miracle.modules.merchant.mapper.MerchantUserMapper;
import com.example.miracle.modules.merchant.service.MerchantUserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 商户用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class MerchantUserServiceImpl extends ServiceImpl<MerchantUserMapper, MerchantUser> implements MerchantUserService {

    private final JwtUtil jwtUtil;

    private final WxMaService wxMaService;

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

    @Override
    @SneakyThrows
    public SingleResponse<MerchantUserLoginDTO> phoneLogin(WxPhoneLoginDTO wxPhoneLoginDTO) {
        // 1. 通过 code 获取手机号
        WxMaPhoneNumberInfo phoneNumberInfo = wxMaService.getUserService().getNewPhoneNoInfo(wxPhoneLoginDTO.getCode());

        if (phoneNumberInfo == null || StringUtils.isEmpty(phoneNumberInfo.getPhoneNumber())) {
            return SingleResponse.buildFailure("获取手机号失败");
        }

        String phone = phoneNumberInfo.getPhoneNumber();

        LambdaQueryWrapper<MerchantUser> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.eq(MerchantUser::getUsername, phone);

        // 2. 查询用户是否存在
        MerchantUser user = this.getOne(LambdaQueryWrapper);

        // 3. 用户不存在则注册
        if (user == null) {
            user = new MerchantUser();
            user.setUsername(phone); // 默认使用手机号作为用户名
            user.setPassword(phone); // 默认密码
            user.setStatus(1); // 正常状态

            // 保存用户
            save(user);
        }

        // 4. 生成 token
        String token = jwtUtil.generateToken(user);

        // 5. 构建返回结果
        // 构建返回对象
        MerchantUserLoginDTO loginDTO = new MerchantUserLoginDTO();
        loginDTO.setUsername(user.getUsername());
        loginDTO.setToken(token);
        loginDTO.setRole(CommonConstant.MERCHANT_ROLE);
        return SingleResponse.of(loginDTO);

    }

} 