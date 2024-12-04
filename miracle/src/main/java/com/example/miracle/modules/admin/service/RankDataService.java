package com.example.miracle.modules.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.admin.dto.RankDataDTO;
import com.example.miracle.modules.admin.entity.RankData;

import java.time.LocalDate;
import java.util.List;

public interface RankDataService extends IService<RankData> {

    /**
     * 生成排行榜数据
     */
    void generateRankData(Long ruleId, LocalDate date);

    /**
     * 获取排行榜数据
     */
    List<RankData> getRankData(Long ruleId, LocalDate date);

    /**
     * 分页查询排行榜数据
     */
    Page<RankData> pageRankData(Long ruleId, LocalDate startDate, LocalDate endDate,
                                Integer current, Integer size);

    /**
     * 获取最新排行榜
     */
    List<RankDataDTO> getLatestRank(Long ruleId, Integer limit);

    /**
     * 获取指定日期的排行榜
     */
    List<RankDataDTO> getRankByDate(Long ruleId, LocalDate date, Integer limit);

    /**
     * 获取排行趋势
     */
    List<RankDataDTO> getRankTrend(Long ruleId, Long targetId, LocalDate startDate, LocalDate endDate);
}