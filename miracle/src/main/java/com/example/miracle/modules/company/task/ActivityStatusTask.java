package com.example.miracle.modules.company.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.miracle.modules.company.entity.Activity;
import com.example.miracle.modules.company.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动状态更新定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityStatusTask {

    private final ActivityService activityService;

    /**
     * 每分钟执行一次活动状态更新
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新未开始的活动为进行中
        activityService.update(
            new LambdaUpdateWrapper<Activity>()
                .set(Activity::getStatus, 1)
                .eq(Activity::getStatus, 0)
                .le(Activity::getStartTime, now)
                .ge(Activity::getEndTime, now)
        );

        // 更新已结束的活动状态
        activityService.update(
            new LambdaUpdateWrapper<Activity>()
                .set(Activity::getStatus, 2)
                .lt(Activity::getEndTime, now)
                .ne(Activity::getStatus, 2)
        );

        // 记录日志
        log.info("活动状态更新完成: {}", now);
    }

    /**
     * 每天凌晨1点执行一次活动状态检查
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkActivityStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询状态异常的活动
        List<Activity> activities = activityService.list(
            new LambdaQueryWrapper<Activity>()
                .and(wrapper -> wrapper
                    // 已开始但状态为未开始
                    .or(w -> w
                        .eq(Activity::getStatus, 0)
                        .le(Activity::getStartTime, now)
                    )
                    // 已结束但状态不为已结束
                    .or(w -> w
                        .ne(Activity::getStatus, 2)
                        .lt(Activity::getEndTime, now)
                    )
                    // 进行中但不在时间范围内
                    .or(w -> w
                        .eq(Activity::getStatus, 1)
                        .and(sw -> sw
                            .gt(Activity::getStartTime, now)
                            .or()
                            .lt(Activity::getEndTime, now)
                        )
                    )
                )
        );

        if (!activities.isEmpty()) {
            log.warn("发现{}个活动状态异常", activities.size());
            for (Activity activity : activities) {
                log.warn("活动状态异常 - ID:{}, 标题:{}, 状态:{}, 开始时间:{}, 结束时间:{}",
                    activity.getId(), activity.getTitle(), activity.getStatus(),
                    activity.getStartTime(), activity.getEndTime());
            }
        }
    }
} 