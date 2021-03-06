package com.example.caffeine.configuration;

import com.example.caffeine.cache.UnifiedCacheManager;
import com.example.caffeine.cache.settings.CaffeineCacheSetting;
import com.example.caffeine.constant.CaffeineCacheEnum;
import com.example.caffeine.service.IRedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置
 *
 * @author Alex
 */
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfiguration {

    @Value("${caffeine.cache.property}")
    private String caffeineCacheProperty;

    @Value("${caffeine.default.initial.capacity}")
    private int caffeineDefaultInitialCapacity;

    @Value("${caffeine.default.maximum.size}")
    private int caffeineDefaultMaximumSize;

    @Value("${caffeine.default.expire.duration}")
    private int caffeineDefaultExpireDuration;

    @Value("${caffeine.default.time.unit}")
    private String caffeineDefaultTimeUnit;

    @Value("${caffeine.cache.dynamic.switch}")
    private boolean caffeineCacheDynamicSwitch;

    @Value("${caffeine.cache.allow.null.values}")
    private boolean caffeineCacheAllowNullValues;

    private final CacheProperties cacheProperties;

    public CacheConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    @Primary
    public UnifiedCacheManager unifiedCacheManager(IRedisService redisService) {
        UnifiedCacheManager unifiedCacheManager = new UnifiedCacheManager(redisService, caffeineCacheDynamicSwitch, caffeineCacheAllowNullValues);
        setCaffeineDefaultSetting(unifiedCacheManager);
        setCaffeineCacheSetting(unifiedCacheManager);
        return unifiedCacheManager;
    }

    /**
     * 设置一级缓存默认配置
     *
     * @param unifiedCacheManager 统一缓存管理器
     */
    private void setCaffeineDefaultSetting(final UnifiedCacheManager unifiedCacheManager) {
        unifiedCacheManager.setCaffeineDefaultInitialCapacity(caffeineDefaultInitialCapacity);
        unifiedCacheManager.setCaffeineDefaultMaximumSize(caffeineDefaultMaximumSize);
        unifiedCacheManager.setCaffeineDefaultExpireDuration(caffeineDefaultExpireDuration);
        unifiedCacheManager.setCaffeineDefaultTimeUnit(caffeineDefaultTimeUnit);
    }

    /**
     * 设置一级缓存相关配置
     *
     * @param unifiedCacheManager 统一缓存管理器
     */
    private void setCaffeineCacheSetting(final UnifiedCacheManager unifiedCacheManager) {
        String spec = cacheProperties.getCaffeine().getSpec();
        if (!StringUtils.isBlank(spec)) {
            unifiedCacheManager.setCaffeineSpec(spec);
        }

        Map<String, CaffeineCacheSetting> caffeineCacheSettingMap = new HashMap<>(16);
        // 按类型添加 用户、订单、预约单、购买人、使用人等
        caffeineCacheSettingMap.put(CaffeineCacheEnum.USER.value(), createCaffeineCacheSetting());
        caffeineCacheSettingMap.put(CaffeineCacheEnum.ORDER.value(), createCaffeineCacheSetting());
        caffeineCacheSettingMap.put(CaffeineCacheEnum.APPOINTMENT.value(), createCaffeineCacheSetting());
        caffeineCacheSettingMap.put(CaffeineCacheEnum.PURCHASER.value(), createCaffeineCacheSetting());
        caffeineCacheSettingMap.put(CaffeineCacheEnum.HOLDER.value(), createCaffeineCacheSetting());
        unifiedCacheManager.setCaffeineCacheSettingMap(caffeineCacheSettingMap);
    }

    private CaffeineCacheSetting createCaffeineCacheSetting() {
        return new CaffeineCacheSetting(caffeineCacheProperty);
    }
}
