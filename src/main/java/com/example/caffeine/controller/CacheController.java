package com.example.caffeine.controller;

import com.example.caffeine.bean.CacheBean;
import com.example.caffeine.cache.UnifiedCache;
import com.example.caffeine.cache.UnifiedCacheManager;
import com.example.caffeine.domain.UserBean;
import com.example.caffeine.service.ICacheService;
import com.example.caffeine.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * cache controller
 *
 * @author Alex
 */
@RestController
public class CacheController {

    @Value("${caffeine.cache.property}")
    private String caffeineCacheProperty;

    @Autowired
    private IUserService userService;

    @Autowired
    private UnifiedCacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ICacheService cacheService;

    @GetMapping("/redis")
    public UserBean redis(int id) {
        final String key = "user:redis:" + id;
        Object value = redisTemplate.opsForValue().get(key);
        if (null == value) {
            value = userService.findById(id);
            if (null != value) {
                redisTemplate.opsForValue().set(key, value, 3600L, TimeUnit.SECONDS);
            }
        }
        return (UserBean) value;
    }

    @GetMapping("/get")
    public Object get(String key) {
        UnifiedCache unifiedCache = (UnifiedCache) cacheManager.getCache(key);
        final String cacheKey = key + ":" + 1;
        Cache.ValueWrapper valueWrapper = unifiedCache.get(key);
        return valueWrapper.get();
    }

    @GetMapping("/callable")
    public UserBean callable(String key, int id) {
        UnifiedCache unifiedCache = (UnifiedCache) cacheManager.getCache(key);
        final String cacheKey = key + ":" + 2;
        UserBean userBean = unifiedCache.get(cacheKey, () -> userService.findById(id));
        return userBean;
    }

    @GetMapping("/invalidate")
    public void invalidate(String key) {
        UnifiedCache unifiedCache = (UnifiedCache) cacheManager.getCache(key);
        final String cacheKey = key + ":" + 2;
        CaffeineCache caffeineCache = unifiedCache.getCaffeineCache();
        caffeineCache.getNativeCache().invalidate(cacheKey);
    }

    @GetMapping("/selectAllKeys")
    public Set<Object> selectAllKeys(String key) {
        UnifiedCache unifiedCache = (UnifiedCache) cacheManager.getCache(key);
        return unifiedCache.selectAllKeys();
    }

    @GetMapping("/properties")
    public Map<String, String> properties() {
        return UnifiedCacheManager.buildCacheProperties(caffeineCacheProperty);
    }

    @GetMapping("/queryAllCacheStatus")
    public List<CacheBean> queryAllCacheStatus() {
        return cacheService.queryAllCacheStatus();
    }
}
