package com.aniva.modules.inventory.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class InventoryCacheService {

    private static final String INVENTORY_KEY_PREFIX = "inventory:";
    private static final Duration STOCK_TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, Object> redisTemplate;

    public InventoryCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Integer getStock(Long productId) {

        String key = buildKey(productId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        redisTemplate.expire(key, STOCK_TTL);

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.parseInt(value.toString());
    }

    public void cacheStock(Long productId, Integer availableStock) {
        if (availableStock == null || availableStock < 0) {
            return;
        }
        redisTemplate.opsForValue().set(buildKey(productId), availableStock, STOCK_TTL);
    }

    public void evictStock(Long productId) {
        redisTemplate.delete(buildKey(productId));
    }

    private String buildKey(Long productId) {
        return INVENTORY_KEY_PREFIX + productId;
    }
}
