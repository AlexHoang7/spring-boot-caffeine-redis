package com.example.caffeine.controller;

import com.example.caffeine.bean.CacheBean;
import com.example.caffeine.cache.UnifiedCache;
import com.example.caffeine.cache.UnifiedCacheManager;
import com.example.caffeine.constant.CaffeineCacheEnum;
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
        final String cacheKey = key + ":test" + 1;
        Cache.ValueWrapper valueWrapper = unifiedCache.get(cacheKey);
        return valueWrapper.get();
    }

    @GetMapping("/callable")
    public UserBean callable(String key, int id) {
        UnifiedCache userCache = (UnifiedCache) cacheManager.getCache(key);
        String cacheKey = key + ":test1";
        UserBean userBean = userCache.get(cacheKey, () -> userService.findById(id));
        userCache.get("user:test2", () -> userService.findById(id));
        userCache.get("user:test3", () -> userService.findById(id));
        UnifiedCache orderCache = (UnifiedCache) cacheManager.getCache(CaffeineCacheEnum.ORDER.value());
        cacheKey = "order:test2";
        orderCache.get(cacheKey, () -> userService.findById(3));
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

    /**
     * TODO
     * 在清除了缓存值之后  应该缓存查不到了  但是还是在get方法中获取到   ---- 》   此处是在redis中获取的
     *
     * @param key
     */
    @GetMapping("/invalidateAll")
    public void invalidateAll(String key) {
        UnifiedCache unifiedCache = (UnifiedCache) cacheManager.getCache(key);
        unifiedCache.clear();
//        if (null != unifiedCache) {
//            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = unifiedCache.getCaffeineCache().getNativeCache();
//            nativeCache.invalidateAll();
//        }
    }

    @GetMapping("/cleanUp")
    public void cleanUp(String key) {
        UnifiedCache unifiedCache = (UnifiedCache) cacheManager.getCache("");
        unifiedCache.getCaffeineCache().getNativeCache().invalidateAll();
    }
}
