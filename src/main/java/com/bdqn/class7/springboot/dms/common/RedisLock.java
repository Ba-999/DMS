package com.bdqn.class7.springboot.dms.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 用 SETNX 实现的简易分布式锁，解决宿舍入住/换宿的并发超卖问题。
 * 锁 30 秒自动过期防止死锁，业务耗时远超 30 秒的场景可调。
 */
@Component
@RequiredArgsConstructor
public class RedisLock {

    private final StringRedisTemplate stringRedisTemplate;
    private static final long EXPIRE = 30;

    public boolean lock(String key) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(key, "1", EXPIRE, TimeUnit.SECONDS));
    }

    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
