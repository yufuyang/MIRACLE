package com.example.miracle.common.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLock {
    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(String key, long seconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", seconds, TimeUnit.SECONDS));
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }
} 