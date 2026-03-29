package com.aniva.modules.inventory.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
public class RedisLockService {

    private static final Duration LOCK_TTL = Duration.ofSeconds(5);
    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """,
            Long.class
    );

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLockService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String acquireLock(String key) {
        String value = UUID.randomUUID().toString();
        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(key, value, LOCK_TTL);

        return Boolean.TRUE.equals(acquired) ? value : null;
    }

    public void releaseLock(String key, String value) {
        if (value == null) {
            return;
        }

        redisTemplate.execute(
                RELEASE_LOCK_SCRIPT,
                Collections.singletonList(key),
                value
        );
    }
}
