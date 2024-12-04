package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.Result;
import com.example.miracle.modules.admin.entity.Notice;
import com.example.miracle.modules.admin.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/page")
    public Result<Page<Notice>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return Result.ok(noticeService.pageNotice(current, size, title, type, status));
    }

    @PostMapping
    public Result<?> create(@RequestBody Notice notice) {
        noticeService.createNotice(notice);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.ok();
    }

    @PutMapping("/publish/{id}")
    public Result<?> publish(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return Result.ok();
    }

    @PutMapping("/offline/{id}")
    public Result<?> offline(@PathVariable Long id) {
        noticeService.offlineNotice(id);
        return Result.ok();
    }
}