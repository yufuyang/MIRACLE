package com.example.miracle.modules.company.controller;

import com.example.miracle.common.dto.ResultDTO;
import com.example.miracle.modules.company.dto.CompanyDashboardDTO;
import com.example.miracle.modules.company.dto.CompanyTrendDTO;
import com.example.miracle.modules.company.service.CompanyStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/company/statistics")
@RequiredArgsConstructor
public class CompanyStatisticsController {

    private final CompanyStatisticsService statisticsService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public ResultDTO<CompanyDashboardDTO> getDashboard(
            @RequestAttribute Long companyId) {
        return ResultDTO.ok(statisticsService.getDashboard(companyId));
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public ResultDTO<CompanyTrendDTO> getTrend(
            @RequestAttribute Long companyId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResultDTO.ok(statisticsService.getTrend(companyId, startDate, endDate));
    }
}