package com.example.miracle.modules.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miracle.modules.admin.entity.Notice;

public interface NoticeService extends IService<Notice> {

    /**
     * 创建公告
     */
    void createNotice(Notice notice);

    /**
     * 更新公告
     */
    void updateNotice(Notice notice);

    /**
     * 删除公告
     */
    void deleteNotice(Long id);

    /**
     * 发布公告
     */
    void publishNotice(Long id);

    /**
     * 下线公告
     */
    void offlineNotice(Long id);

    /**
     * 分页查询公告
     */
    Page<Notice> pageNotice(Integer current, Integer size, String title, Integer type, Integer status);
}