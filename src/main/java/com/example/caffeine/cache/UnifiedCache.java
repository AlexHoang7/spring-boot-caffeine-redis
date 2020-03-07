package com.example.caffeine.cache;

import com.example.caffeine.service.IRedisService;
import com.example.caffeine.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;
import org.springframework.lang.NonNull;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * 统一缓存
 *
 * @author Alex
 */
@Slf4j
@SuppressWarnings("unchecked")
public class UnifiedCache extends AbstractValueAdaptingCache {

    private static final long DEFAULT_REDIS_EXPIRE_TIME = 3600L;

    /**
     * 缓存名字
     */
    private final String name;

    /**
     * 是否使用一级缓存
     */
    private final boolean caffeineCacheSwitch;

    /**
     * caffeine cache
     */
    private final CaffeineCache caffeineCache;

    private final IRedisService redisService;

    /**
     * 实现spring cache接口 用于在cacheManager中生成cache对象
     *
     * @param allowNullValues     是否允许缓存空值
     * @param caffeineCacheSwitch 一级缓存开关
     * @param name                缓存名字
     * @param caffeine            caffeine对象
     */
    public UnifiedCache(boolean allowNullValues, boolean caffeineCacheSwitch, String name,
                        IRedisService redisService, Cache<Object, Object> caffeine) {
        super(allowNullValues);
        this.caffeineCacheSwitch = caffeineCacheSwitch;
        this.name = name;
        this.redisService = redisService;
        this.caffeineCache = new CaffeineCache(name, caffeine, allowNullValues);
    }

    /**
     * 获取redis 二级缓存
     *
     * @return redis缓存服务
     */
    public IRedisService getRedisService() {
        return redisService;
    }

    public CaffeineCache getCaffeineCache() {
        return caffeineCache;
    }

    @Override
    protected Object lookup(@NonNull Object key) {
        Object value = null;
        if (caffeineCacheSwitch) {
            value = caffeineCache.get(key);
            log.info("lookup方法----key=[{}]查询一级缓存值为=[{}]", key, value);
        }
        if (null == value) {
            value = redisService.getObject((String) key);
            log.info("lookup方法----key=[{}]查询二级缓存值为=[{}]", key, value);
        }
        return value;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(@NonNull Object key) {
        try {
            ValueWrapper valueWrapper = null;
            if (caffeineCacheSwitch) {
                valueWrapper = caffeineCache.get(key);
                log.info("get方法----key=[{}]查询一级缓存值为=[{}]", key, JsonUtil.toJson(valueWrapper));
            }
            if (null == valueWrapper) {
                valueWrapper = redisService.getCacheValueWrapper(key);
                log.info("get方法----key=[{}]查询二级缓存值为=[{}]", key, JsonUtil.toJson(valueWrapper));
            }
            return valueWrapper;
        } catch (JsonProcessingException e) {
            log.error("json转换异常");
        }
        return super.get(key);
    }

    @Override
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        T value = null;
        // 如果开启一级缓存则直接查询一级缓存 一级缓存没有查询二级缓存
        // 否则直接查询二级缓存
        if (caffeineCacheSwitch) {
            value = (T) caffeineCache.getNativeCache().get(key, k -> queryRedisCache(key, valueLoader));
        } else {
            value = (T) queryRedisCache(key, valueLoader);
        }
        if (value instanceof NullValue) {
            return null;
        }
        return (T) fromStoreValue(value);
    }

    @Override
    public void put(@NonNull Object key, Object value) {
        if (caffeineCacheSwitch) {
            caffeineCache.put(key, value);
        }
        // 注意redis不能直接set值
        redisService.setObject((String) key, value, DEFAULT_REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 删除指定缓存
     *
     * @param key 缓存key
     */
    @Override
    public void evict(@NonNull Object key) {
        // 先删除二级缓存再删除一级缓存 防止并发问题
        // 删除的时候需要通过redis订阅消息的方式告知其他应用更新缓存
        redisService.delete((String) key);
        if (caffeineCacheSwitch) {
            // redis订阅通知其他节点删除
            caffeineCache.evict(key);
        }
    }

    @Override
    public void clear() {
//        慎用 redis clear *
        // 先清除二级缓存
        caffeineCache.clear();
    }

    @Override
    public boolean invalidate() {
        // 需先清除二级缓存
        return caffeineCache.invalidate();
    }

    /**
     * 查询二级缓存
     *
     * @param key         缓存key
     * @param valueLoader 值加载器 从其他数据源获取 此处从redis获取不到之后从db拿
     * @param <T>         结果泛型
     * @return 最后结果
     */
    private <T> Object queryRedisCache(Object key, Callable<T> valueLoader) {
        // 先从二级缓存中拿 如果没有则从db拿
        // 此处key要跟一级缓存key做区别处理  改为redisKey
        T value = (T) redisService.getObject((String) key);
        log.info("一级缓存key=[{}]获取二级缓存值为=[{}]", key, value);
        if (null == value) {
            try {
                value = valueLoader.call();
            } catch (Exception e) {
                log.error("----------------------------执行db方法异常-------------------------");
            }
            log.info("一级缓存key=[{}]执行db方法查询的值为=[{}]", key, value);
            if (null != value) {
                redisService.setObject((String) key, value, DEFAULT_REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
            }
        }
        return toStoreValue(value);
    }

    /**
     * 查看所有缓存键
     *
     * @return 所有缓存键
     */
    public Set<Object> selectAllKeys() {
        return getCaffeineCache().getNativeCache().asMap().keySet();
    }
}
