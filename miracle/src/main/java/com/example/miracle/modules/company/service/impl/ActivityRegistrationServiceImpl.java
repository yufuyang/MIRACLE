package com.example.miracle.modules.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.dto.MultiResponse;
import com.example.miracle.common.dto.SingleResponse;
import com.example.miracle.modules.company.dto.ActivityRegistrationDTO;
import com.example.miracle.modules.company.dto.cmd.ActivityRegistrationAuditCmd;
import com.example.miracle.modules.company.dto.query.ActivityRegistrationPageQry;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.entity.ActivityRegistration;
import com.example.miracle.modules.company.entity.ActivityStats;
import com.example.miracle.modules.company.mapper.ActivityRegistrationMapper;
import com.example.miracle.modules.company.service.ActivityRegistrationService;
import com.example.miracle.modules.company.service.ActivityService;
import com.example.miracle.modules.company.service.ActivityStatsService;
import com.example.miracle.modules.merchant.dto.cmd.MerchantRegisterActivityCmd;
import com.example.miracle.modules.platform.entity.Merchant;
import com.example.miracle.modules.platform.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityRegistrationServiceImpl extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> implements ActivityRegistrationService {


    private final MerchantService merchantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse register(MerchantRegisterActivityCmd merchantRegisterActivityCmd) {

        // 检查是否已经报名
        Long count = this.count(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, merchantRegisterActivityCmd.getActivityId())
                .eq(ActivityRegistration::getMerchantId, merchantRegisterActivityCmd.getMerchantId())
        );
        if (count > 0) {
            return SingleResponse.buildFailure("已报名");
        }

        // 创建报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(merchantRegisterActivityCmd.getActivityId());
        registration.setMerchantId(merchantRegisterActivityCmd.getMerchantId());
        registration.setStatus(0);
        this.save(registration);

        return SingleResponse.buildSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse audit(ActivityRegistrationAuditCmd activityRegistrationAuditCmd) {

        ActivityRegistration registration = this.getById(activityRegistrationAuditCmd.getId());
        if (registration == null || registration.getStatus() != 0) {
            return SingleResponse.buildFailure("无效的报名记录");
        }
        // 更新报名状态
        registration.setStatus(activityRegistrationAuditCmd.getStatus());
        this.updateById(registration);


        return SingleResponse.buildSuccess();
    }


    @Override
    public MultiResponse<ActivityRegistrationDTO> listRegistrations(ActivityRegistrationPageQry activityRegistrationPageQry) {
        // 构建查询条件
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        if (activityRegistrationPageQry.getActivityId() != null) {
            wrapper.eq(ActivityRegistration::getActivityId, activityRegistrationPageQry.getActivityId());
        }
        if (activityRegistrationPageQry.getMerchantId() != null) {
            wrapper.eq(ActivityRegistration::getMerchantId, activityRegistrationPageQry.getMerchantId());
        }
        if (activityRegistrationPageQry.getStatus() != null) {
            wrapper.eq(ActivityRegistration::getStatus, activityRegistrationPageQry.getStatus());
        }
        wrapper.orderByDesc(ActivityRegistration::getCreateTime);

        // 分页查询
        Page<ActivityRegistration> page = this.page(new Page<>(activityRegistrationPageQry.getPageNum(), activityRegistrationPageQry.getPageSize()), wrapper);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return MultiResponse.buildSuccess();
        }
        // 获取商户信息
        List<Long> merchantIds = page.getRecords().stream().map(ActivityRegistration::getMerchantId).distinct().collect(Collectors.toList());

        Map<Long, Merchant> merchantMap = merchantService.list(new LambdaQueryWrapper<Merchant>().in(Merchant::getId, merchantIds)).stream().collect(Collectors.toMap(Merchant::getId, merchant -> merchant));

        // 组装数据
        List<ActivityRegistrationDTO> dtoList = new ArrayList<>();

        page.getRecords().forEach(registration -> {

            ActivityRegistrationDTO dto = new ActivityRegistrationDTO();
            BeanUtils.copyProperties(registration, dto);


            // 设置商户信息
            Merchant merchant = merchantMap.get(registration.getMerchantId());
            if (merchant != null) {

                dto.setMerchantName(merchant.getMerchantName());
            }

            dtoList.add(dto);
        });

        return MultiResponse.of(dtoList, (int) page.getTotal());
    }
} 