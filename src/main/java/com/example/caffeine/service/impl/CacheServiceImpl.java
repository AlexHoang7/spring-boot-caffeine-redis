package com.example.caffeine.service.impl;

import com.example.caffeine.bean.CacheBean;
import com.example.caffeine.cache.UnifiedCache;
import com.example.caffeine.cache.UnifiedCacheManager;
import com.example.caffeine.service.ICacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 缓存服务
 *
 * @author Alex
 */
@Slf4j
@Service
public class CacheServiceImpl implements ICacheService {

    @Value("${caffeine.cache.property}")
    private String caffeineCacheProperty;

    private final UnifiedCacheManager unifiedCacheManager;

    public CacheServiceImpl(UnifiedCacheManager unifiedCacheManager) {
        this.unifiedCacheManager = unifiedCacheManager;
    }

    @Override
    public List<CacheBean> queryAllCacheStatus() {
        List<String> cacheNameList = new ArrayList<>(unifiedCacheManager.getCacheNames());
        List<CacheBean> cacheBeans = new ArrayList<>();
        cacheNameList.stream().sorted().forEach(cacheName -> {
            cacheBeans.add(queryCacheStatus(cacheName));
        });
        return cacheBeans;
    }

    @Override
    public Cache<Object, Object> queryCacheName(String cacheName) {
        UnifiedCache unifiedCache = (UnifiedCache) unifiedCacheManager.getCache(cacheName);
        return unifiedCache.getCaffeineCache().getNativeCache();
//        // 得到项目自己配置的cache
//        return unifiedCacheManager.createNativeCaffeineCache(cacheName);
    }

    @Override
    public Set<String> queryAllCacheNames() {
        return unifiedCacheManager.getCacheConcurrentMap().keySet();
    }

    @Override
    public CacheBean queryCacheStatus(String cacheName) {
        Cache<Object, Object> cache = queryCacheName(cacheName);
        CacheStats cacheStats = cache.stats();
        // 百分比格式化
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        // 百分比小数点后的位数
        numberFormat.setMaximumFractionDigits(1);
        Map<String, String> propertiesMap = UnifiedCacheManager.buildCacheProperties(caffeineCacheProperty);
        CacheBean cacheBean = new CacheBean();
        cacheBean.setCacheName(cacheName);
        cacheBean.setSize(cache.estimatedSize());
        // TODO 修改为动态取
        cacheBean.setInitialCapacity(Integer.parseInt(propertiesMap.get("initialCapacity")));
        cacheBean.setMaximumSize(Integer.parseInt(propertiesMap.get("maximumSize")));
        cacheBean.setSurvivalDuration(propertiesMap.get("expireAfterAccess"));
        cacheBean.setHitCount(cacheStats.hitCount());
        cacheBean.setHitRate(numberFormat.format(cacheStats.hitRate()));
        cacheBean.setMissCount(cacheStats.missCount());
        cacheBean.setMissRate(numberFormat.format(cacheStats.missRate()));
        cacheBean.setLoadSuccessCount(cacheStats.loadSuccessCount());
        cacheBean.setLoadFailureCount(cacheStats.loadFailureCount());
        cacheBean.setLoadFailureRate(numberFormat.format(cacheStats.loadFailureRate()));
        cacheBean.setTotalLoadTime(cacheStats.totalLoadTime() / 1000);
        if (!CollectionUtils.isEmpty(unifiedCacheManager.getCacheConcurrentMap())) {
            cacheBean.setResetTime(unifiedCacheManager.getResetTime());
        }
        return cacheBean;
    }

    @Override
    public String queryRedisKey(String cacheName) {
        
        return null;
    }
}
