package com.example.caffeine.service;

import com.example.caffeine.bean.CacheBean;
import com.github.benmanes.caffeine.cache.Cache;

import java.util.List;
import java.util.Set;

/**
 * 缓存服务接口
 *
 * @author Alex
 */
public interface ICacheService {

    /**
     * 查询所有缓存状态
     *
     * @return 缓存状态集合
     */
    List<CacheBean> queryAllCacheStatus();

    /**
     * 获取缓存名字
     *
     * @param cacheName 缓存名称
     * @return 缓存
     */
    Cache<Object, Object> queryCacheName(final String cacheName);

    /**
     * 获取缓存名字
     *
     * @return 缓存名字集合
     */
    Set<String> queryAllCacheNames();

    /**
     * 查询缓存状态
     *
     * @param cacheName 缓存名称
     * @return 缓存bean
     */
    CacheBean queryCacheStatus(final String cacheName);

    /**
     * 查询redis key名称
     *
     * @param cacheName 一级缓存名称
     * @return redis key名称
     */
    String queryRedisKey(final String cacheName);
}
