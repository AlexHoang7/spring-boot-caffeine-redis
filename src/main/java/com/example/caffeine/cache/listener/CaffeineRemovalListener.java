package com.example.caffeine.cache.listener;

import com.example.caffeine.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * caffeine 移除监听
 *
 * @author Alex
 */
@Slf4j
public class CaffeineRemovalListener implements RemovalListener<Object, Object> {

    @Override
    public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {
        try {
            log.info("--------------caffeine remove cache key=[{}], value=[{}], cause=[{}]",
                    key, JsonUtil.toJson(value), cause);
        } catch (JsonProcessingException e) {
            log.error("json processing error");
        }
    }
}
