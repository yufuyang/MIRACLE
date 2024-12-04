package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.admin.dto.RankDataDTO;
import com.example.miracle.modules.admin.entity.RankRule;
import com.example.miracle.modules.company.service.CompanyRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/company/rank")
@RequiredArgsConstructor
public class CompanyRankController {

    private final CompanyRankService companyRankService;

    /**
     * 获取排行规则列表
     */
    @GetMapping("/rules")
    public Result<List<RankRule>> listRules(
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(required = false) Integer timeType) {
        return Result.ok(companyRankService.listRules(ruleType, timeType));
    }

    /**
     * 获取最新排行榜
     */
    @GetMapping("/latest/{ruleId}")
    public Result<List<RankDataDTO>> getLatestRank(
            @RequestAttribute Long companyId,
            @PathVariable Long ruleId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(companyRankService.getLatestRank(companyId, ruleId, limit));
    }

    /**
     * 获取指定日期的排行榜
     */
    @GetMapping("/{ruleId}")
    public Result<List<RankDataDTO>> getRankByDate(
            @RequestAttribute Long companyId,
            @PathVariable Long ruleId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(companyRankService.getRankByDate(companyId, ruleId, date, limit));
    }

    /**
     * 获取排名趋势
     */
    @GetMapping("/trend/{ruleId}/{targetId}")
    public Result<List<RankDataDTO>> getRankTrend(
            @RequestAttribute Long companyId,
            @PathVariable Long ruleId,
            @PathVariable Long targetId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.ok(companyRankService.getRankTrend(companyId, ruleId, targetId, startDate, endDate));
    }
}