package com.example.caffeine.service;

import org.springframework.cache.Cache;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 *
 * @author Alex
 */
public interface IRedisService {

    /**
     * 设置值
     *
     * @param key      键值
     * @param value    需缓存的值
     * @param expire   过期时间
     * @param timeUnit 时间单位
     */
    void setObject(final String key, final Object value, final long expire, final TimeUnit timeUnit);

    /**
     * 从缓存中取
     *
     * @param key 键值
     * @return 缓存的值
     */
    Object getObject(final String key);

    /**
     * 删除缓存
     *
     * @param key 键值
     */
    void delete(final String key);

    /**
     * 清除所有缓存
     */
    void clear();

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值
     */
    Object getCacheObject(final Object key);

    /**
     * 获取结果包装
     *
     * @param key 缓存键
     * @return 包装值
     */
    Cache.ValueWrapper getCacheValueWrapper(final Object key);
}
