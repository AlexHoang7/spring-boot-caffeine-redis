package com.example.caffeine.service.impl;

import com.example.caffeine.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 二级缓存实现类
 *
 * @author Alex
 */
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setObject(String key, Object value, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    @Override
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }


    @Override
    public void clear() {
        // 慎用
    }

    @Override
    public Object getCacheObject(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Cache.ValueWrapper getCacheValueWrapper(Object key) {
        Object value = getCacheObject(key);
        return new RedisValueWrapper(value);
    }

    static class RedisValueWrapper implements Cache.ValueWrapper {

        private final Object object;

        public RedisValueWrapper(Object object) {
            this.object = object;
        }

        @Override
        public Object get() {
            return object;
        }
    }
}
