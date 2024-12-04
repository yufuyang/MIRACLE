package com.example.miracle.modules.admin.controller;


import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.admin.dto.RankDataDTO;
import com.example.miracle.modules.admin.service.RankDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/rank/data")
@RequiredArgsConstructor
public class RankDataController {

    private final RankDataService rankDataService;

    @GetMapping("/latest/{ruleId}")
    public Result<List<RankDataDTO>> getLatestRank(
            @PathVariable Long ruleId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(rankDataService.getLatestRank(ruleId, limit));
    }

    @GetMapping("/{ruleId}")
    public Result<List<RankDataDTO>> getRankByDate(
            @PathVariable Long ruleId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.ok(rankDataService.getRankByDate(ruleId, date, limit));
    }

    @GetMapping("/trend/{ruleId}/{targetId}")
    public Result<List<RankDataDTO>> getRankTrend(
            @PathVariable Long ruleId,
            @PathVariable Long targetId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.ok(rankDataService.getRankTrend(ruleId, targetId, startDate, endDate));
    }
}
