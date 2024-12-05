package com.example.miracle.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.admin.dto.RankDataDTO;
import com.example.miracle.modules.admin.entity.RankData;
import com.example.miracle.modules.admin.entity.RankRule;
import com.example.miracle.modules.admin.mapper.RankDataMapper;
import com.example.miracle.modules.admin.service.RankDataService;
import com.example.miracle.modules.admin.service.RankRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankDataServiceImpl extends ServiceImpl<RankDataMapper, RankData> implements RankDataService {

    private final RankRuleService rankRuleService;
    private final RankDataMapper rankDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateRankData(Long ruleId, LocalDate date) {
        // 获取规则
        RankRule rule = rankRuleService.getById(ruleId);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }

        // 验证规则状态
        if (rule.getStatus() != 1) {
            throw new BusinessException("规则已禁用");
        }

        // 删除当天的排行数据
        this.remove(new LambdaQueryWrapper<RankData>()
                .eq(RankData::getRuleId, ruleId)
                .eq(RankData::getRankDate, date));

        // 获取排行数据
        List<RankData> rankDataList;
        LocalDate startDate = date;
        LocalDate endDate = date.plusDays(1);

        switch (rule.getRuleType()) {
            case 1: // 销售排行
                rankDataList = rankDataMapper.getMerchantSalesRank(startDate, endDate);
                break;
            case 2: // 商品排行
                rankDataList = rankDataMapper.getProductSalesRank(startDate, endDate);
                break;
            default:
                throw new BusinessException("不支持的规则类型");
        }

        // 设置排名
        for (int i = 0; i < rankDataList.size(); i++) {
            RankData rankData = rankDataList.get(i);
            rankData.setRuleId(ruleId);
            rankData.setRankNo(i + 1);
            rankData.setRankDate(date);
        }

        // 保存排行数据
        this.saveBatch(rankDataList);
    }

    @Override
    public List<RankData> getRankData(Long ruleId, LocalDate date) {
        return this.list(new LambdaQueryWrapper<RankData>()
                .eq(RankData::getRuleId, ruleId)
                .eq(RankData::getRankDate, date)
                .orderByAsc(RankData::getRankNo));
    }

    @Override
    public Page<RankData> pageRankData(Long ruleId, LocalDate startDate, LocalDate endDate,
                                       Integer current, Integer size) {
        return this.page(new Page<>(current, size),
                new LambdaQueryWrapper<RankData>()
                        .eq(RankData::getRuleId, ruleId)
                        .ge(startDate != null, RankData::getRankDate, startDate)
                        .le(endDate != null, RankData::getRankDate, endDate)
                        .orderByDesc(RankData::getRankDate)
                        .orderByAsc(RankData::getRankNo));
    }

    @Override
    public List<RankDataDTO> getLatestRank(Long ruleId, Integer limit) {
        // 获取最新排名日期
        LocalDate latestDate = this.getOne(new LambdaQueryWrapper<RankData>()
                        .eq(RankData::getRuleId, ruleId)
                        .orderByDesc(RankData::getRankDate)
                        .last("LIMIT 1"))
                .getRankDate();

        return getRankByDate(ruleId, latestDate, limit);
    }

    @Override
    public List<RankDataDTO> getRankByDate(Long ruleId, LocalDate date, Integer limit) {
        // 获取当前排名
        List<RankData> currentRank = this.list(new LambdaQueryWrapper<RankData>()
                .eq(RankData::getRuleId, ruleId)
                .eq(RankData::getRankDate, date)
                .orderByAsc(RankData::getRankNo)
                .last(limit != null, "LIMIT " + limit));

        // 获取上一期排名
        LocalDate prevDate = getPreviousRankDate(ruleId, date);
        Map<Long, Integer> prevRankMap = new HashMap<>();
        if (prevDate != null) {
            this.list(new LambdaQueryWrapper<RankData>()
                            .eq(RankData::getRuleId, ruleId)
                            .eq(RankData::getRankDate, prevDate))
                    .forEach(data -> prevRankMap.put(data.getTargetId(), data.getRankNo()));
        }

        // 转换为DTO并计算变化
        return currentRank.stream().map(data -> {
            RankDataDTO dto = new RankDataDTO();
            dto.setRankNo(data.getRankNo());
            dto.setTargetId(data.getTargetId());
            dto.setTargetName(data.getTargetName());
            dto.setRankValue(data.getRankValue());
            dto.setRankDate(data.getRankDate());

            // 计算排名变化
            Integer prevRank = prevRankMap.get(data.getTargetId());
            if (prevRank != null) {
                int change = prevRank - data.getRankNo();
                dto.setChange(change);
                dto.setTrend(Integer.compare(change, 0));
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RankDataDTO> getRankTrend(Long ruleId, Long targetId, LocalDate startDate, LocalDate endDate) {
        // 验证时间范围
        if (startDate.plusMonths(3).isBefore(endDate)) {
            throw new BusinessException("查询时间范围不能超过3个月");
        }
        
        // 验证规则是否存在且启用
        RankRule rule = rankRuleService.getById(ruleId);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }
        if (rule.getStatus() != 1) {
            throw new BusinessException("规则已禁用");
        }

        // 查询数据
        List<RankData> rankList = this.list(new LambdaQueryWrapper<RankData>()
                .eq(RankData::getRuleId, ruleId)
                .eq(RankData::getTargetId, targetId)
                .ge(RankData::getRankDate, startDate)
                .le(RankData::getRankDate, endDate)
                .orderByAsc(RankData::getRankDate));

        return convertToDTO(rankList);
    }

    /**
     * 获取上一期排名日期
     */
    private LocalDate getPreviousRankDate(Long ruleId, LocalDate date) {
        return this.getOne(new LambdaQueryWrapper<RankData>()
                        .eq(RankData::getRuleId, ruleId)
                        .lt(RankData::getRankDate, date)
                        .orderByDesc(RankData::getRankDate)
                        .last("LIMIT 1"))
                .getRankDate();
    }

    /**
     * 转换为DTO
     */
    private List<RankDataDTO> convertToDTO(List<RankData> rankList) {
        return rankList.stream().map(data -> {
            RankDataDTO dto = new RankDataDTO();
            dto.setRankNo(data.getRankNo());
            dto.setTargetId(data.getTargetId());
            dto.setTargetName(data.getTargetName());
            dto.setRankValue(data.getRankValue());
            dto.setRankDate(data.getRankDate());
            return dto;
        }).collect(Collectors.toList());
    }
}