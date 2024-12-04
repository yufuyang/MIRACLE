package com.example.miracle.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miracle.common.exception.BusinessException;
import com.example.miracle.modules.admin.entity.Notice;
import com.example.miracle.modules.admin.mapper.NoticeMapper;
import com.example.miracle.modules.admin.service.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public void createNotice(Notice notice) {
        notice.setStatus(0); // 默认未发布
        this.save(notice);
    }

    @Override
    public void updateNotice(Notice notice) {
        Notice existNotice = this.getById(notice.getId());
        if (existNotice == null) {
            throw new BusinessException("公告不存在");
        }
        if (existNotice.getStatus() == 1) {
            throw new BusinessException("已发布的公告不能修改");
        }
        this.updateById(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (notice.getStatus() == 1) {
            throw new BusinessException("已发布的公告不能删除");
        }
        this.removeById(id);
    }

    @Override
    public void publishNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (notice.getStatus() == 1) {
            throw new BusinessException("公告已发布");
        }

        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        this.updateById(notice);
    }

    @Override
    public void offlineNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        if (notice.getStatus() != 1) {
            throw new BusinessException("公告未发布");
        }

        notice.setStatus(2);
        this.updateById(notice);
    }

    @Override
    public Page<Notice> pageNotice(Integer current, Integer size, String title, Integer type, Integer status) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(title), Notice::getTitle, title)
                .eq(type != null, Notice::getType, type)
                .eq(status != null, Notice::getStatus, status)
                .orderByDesc(Notice::getCreateTime);

        return this.page(new Page<>(current, size), wrapper);
    }
}