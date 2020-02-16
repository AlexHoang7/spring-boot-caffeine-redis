package com.example.caffeine.cache;

import com.example.caffeine.cache.listener.CaffeineRemovalListener;
import com.example.caffeine.cache.settings.CaffeineCacheSetting;
import com.example.caffeine.cache.settings.RedisCacheSetting;
import com.example.caffeine.constant.ResponseEnum;
import com.example.caffeine.exception.GlobalException;
import com.example.caffeine.service.IRedisService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 统一缓存管理
 *
 * @author Alex
 */
@Slf4j
public class UnifiedCacheManager implements CacheManager {

//    private static final long CAFFEINE_EXPIRE_AFTER_WRITE = 1L;
//
//    private static final int CAFFEINE_INITIAL_CAPACITY = 100;
//
//    public static final int CAFFEINE_MAXIMUM_SIZE = 200;

    @Getter
    @Setter
    private int caffeineDefaultInitialCapacity;

    @Getter
    @Setter
    private int caffeineDefaultMaximumSize;

    @Getter
    @Setter
    private int caffeineDefaultExpireDuration;

    @Getter
    @Setter
    private String caffeineDefaultTimeUnit;

    @Getter
    private Date resetTime;

    /**
     * 存取缓存
     */
    @Getter
    private final Map<String, Cache> cacheConcurrentMap = new ConcurrentHashMap<>(16);

    /**
     * 一级缓存配置
     */
    private Map<String, CaffeineCacheSetting> caffeineCacheSettingMap = null;

    /**
     * 二级缓存配置
     */
    private Map<String, RedisCacheSetting> redisCacheSettingMap = null;

    /**
     * 是否动态创建缓存 默认为true
     */
    private boolean dynamicSwitch;

    /**
     * 是否允许缓存值为null
     */
    private boolean allowNullValues;

    private final IRedisService redisService;

    public UnifiedCacheManager(IRedisService redisService, boolean dynamicSwitch, boolean allowNullValues) {
        this(redisService, Collections.emptyList(), dynamicSwitch, allowNullValues);
    }

//    public UnifiedCacheManager(IRedisService redisService, Collection<String> cacheNames, boolean dynamicSwitch) {
//        this(redisService, cacheNames, false);
//    }

    public UnifiedCacheManager(IRedisService redisService, Collection<String> cacheNames, boolean dynamicSwitch, boolean allowNullValues) {
        this.redisService = redisService;
        this.dynamicSwitch = dynamicSwitch;
        this.allowNullValues = allowNullValues;

        setCacheNames(cacheNames);
    }

    /**
     * 设置缓存
     * 初始化UnifiedCacheManager时初始化缓存
     * 在初始化cacheManager时就会初始化数据 并且在运行时不再创建更多的缓存
     * 使用空的集合或者重新在配置中指定dynamicSwitch后，就可以重新在运行时动态地创建缓存
     *
     * @param cacheNames 缓存名称集合
     */
    private void setCacheNames(final Collection<String> cacheNames) {
        if (null != cacheNames) {
            cacheNames.forEach(cacheName -> cacheConcurrentMap.put(cacheName, createCache(cacheName)));
            dynamicSwitch = cacheNames.isEmpty();
        }
    }

    /**
     * 设置默认caffeine缓存对象
     */
    private Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .expireAfterAccess(caffeineDefaultExpireDuration, TimeUnit.SECONDS)
            .initialCapacity(caffeineDefaultInitialCapacity)
            .maximumSize(caffeineDefaultMaximumSize)
            .removalListener(new CaffeineRemovalListener())
            .recordStats();

    /**
     * 生成缓存对象
     *
     * @param name 缓存key
     * @return spring cache缓存对象
     */
    private Cache createCache(final String name) {
        return new UnifiedCache(allowNullValues, dynamicSwitch, name,
                redisService, createNativeCaffeineCache(name));
    }

    /**
     * 生成原始caffeine缓存
     *
     * @param name cacheName
     * @return 原始caffeine缓存
     */
    protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(final String name) {
        return getCaffeine(name)
                .removalListener(new CaffeineRemovalListener())
                .recordStats()
                .build();
    }

    /**
     * 获取caffeine对象
     *
     * @param name 键值
     * @return 返回我caffeine对象  如果有配置则根据配置 否则按照默认配置
     */
    private Caffeine<Object, Object> getCaffeine(final String name) {
        if (!CollectionUtils.isEmpty(caffeineCacheSettingMap)) {
            CaffeineCacheSetting caffeineCacheSetting = caffeineCacheSettingMap.get(name);
            if (null != caffeineCacheSetting && !StringUtils.isBlank(caffeineCacheSetting.getSpec())) {
                return Caffeine.from(CaffeineSpec.parse(caffeineCacheSetting.getSpec()));
            }
        }
        return caffeine;
    }

    public void setCaffeineCacheSettingMap(Map<String, CaffeineCacheSetting> caffeineCacheSettingMap) {
        this.caffeineCacheSettingMap = !CollectionUtils.isEmpty(caffeineCacheSettingMap) ?
                new ConcurrentHashMap<>(caffeineCacheSettingMap) : null;
    }

    public void setRedisCacheSettingMap(Map<String, RedisCacheSetting> redisCacheSettingMap) {
        this.redisCacheSettingMap = !CollectionUtils.isEmpty(redisCacheSettingMap) ?
                new ConcurrentHashMap<>(redisCacheSettingMap) : null;
    }

    /**
     * 设置一级缓存caffeine配置
     *
     * @param spec 配置值
     */
    public void setCaffeineSpec(final String spec) {
        Caffeine<Object, Object> caffeine = Caffeine.from(CaffeineSpec.parse(spec));
        if (!ObjectUtils.nullSafeEquals(this.caffeine, caffeine)) {
            this.caffeine = caffeine;
            refreshKnownCaches();
        }
    }

    /**
     * 用当前状态重新创建已知缓存
     */
    private void refreshKnownCaches() {
        for (Map.Entry<String, Cache> entry : cacheConcurrentMap.entrySet()) {
            entry.setValue(createCache(entry.getKey()));
        }
    }

    @Override
    public Cache getCache(@NonNull String name) {
        if (StringUtils.isBlank(name)) {
            throw new GlobalException(ResponseEnum.RESPONSE_CODE_99.value(), "未指定查询一级缓存key");
        }
        Cache cache = cacheConcurrentMap.get(name);
        if (null == cache && dynamicSwitch) {
            synchronized (cacheConcurrentMap) {
                cache = cacheConcurrentMap.get(name);
                if (null == cache) {
                    cache = createCache(name);
                    cacheConcurrentMap.put(name, cache);
                    resetTime = new Date();
                }
            }
        }
        return cache;
    }

    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheConcurrentMap.keySet());
    }

    /**
     * 构建缓存配置
     *
     * @param spec 缓存配置表达式
     * @return 缓存配置 map形式
     */
    public static Map<String, String> buildCacheProperties(final String spec) {
        if (StringUtils.isBlank(spec)) {
            throw new GlobalException(ResponseEnum.RESPONSE_CODE_99.value(), "未发现caffeine spec配置，请在properties配置文件中配置");
        }
        String[] specArr = spec.split(",");
        Map<String, String> propertiesMap = new HashMap<>(16);
        Stream.of(specArr).forEach(specStr -> buildSingleProperty(specStr, propertiesMap));
        return propertiesMap;
    }

    /**
     * 添加缓存配置
     *
     * @param specStr       单个缓存配置
     * @param propertiesMap 缓存配置map
     */
    private static void buildSingleProperty(final String specStr, final Map<String, String> propertiesMap) {
        String[] specArr = specStr.split("=");
        propertiesMap.put(specArr[0], specArr[1]);
    }
}
