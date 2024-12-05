package com.example.miracle.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.miracle.common.dto.ResultDTO;
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
    public ResultDTO<Page<Notice>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return ResultDTO.ok(noticeService.pageNotice(current, size, title, type, status));
    }

    @PostMapping
    public ResultDTO<?> create(@RequestBody Notice notice) {
        noticeService.createNotice(notice);
        return ResultDTO.ok();
    }

    @PutMapping
    public ResultDTO<?> update(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return ResultDTO.ok();
    }

    @DeleteMapping("/{id}")
    public ResultDTO<?> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResultDTO.ok();
    }

    @PutMapping("/publish/{id}")
    public ResultDTO<?> publish(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return ResultDTO.ok();
    }

    @PutMapping("/offline/{id}")
    public ResultDTO<?> offline(@PathVariable Long id) {
        noticeService.offlineNotice(id);
        return ResultDTO.ok();
    }
}