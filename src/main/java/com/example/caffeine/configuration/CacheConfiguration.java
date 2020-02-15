package com.example.caffeine.configuration;

import com.example.caffeine.cache.UnifiedCacheManager;
import com.example.caffeine.cache.settings.CaffeineCacheSetting;
import com.example.caffeine.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

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

    @Autowired
    private CacheProperties cacheProperties;

    @Bean
    @Primary
    public CacheManager cacheManager(IRedisService redisService) {
        UnifiedCacheManager unifiedCacheManager = new UnifiedCacheManager(redisService);
        setCaffeineCacheSetting(unifiedCacheManager);
        return unifiedCacheManager;
    }

    /**
     * 设置一级缓存相关配置
     * 按类型添加 用户、订单、预约单、购买人、使用人等
     *
     * @param unifiedCacheManager 统一缓存管理器
     */
    private void setCaffeineCacheSetting(final UnifiedCacheManager unifiedCacheManager) {
        String spec = cacheProperties.getCaffeine().getSpec();
        if (StringUtils.hasText(spec)) {
            unifiedCacheManager.setCaffeineSpec(spec);
        }

        Map<String, CaffeineCacheSetting> caffeineCacheSettingMap = new HashMap<>(16);
        caffeineCacheSettingMap.put("user", new CaffeineCacheSetting("initialCapacity=100,maximumSize=200,expireAfterWrite=60s"));
        unifiedCacheManager.setCaffeineCacheSettingMap(caffeineCacheSettingMap);
    }
}
