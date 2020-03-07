package com.example.caffeine.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * redis 推送监听
 *
 * @author Alex
 */
@Slf4j
public class RedisPublishListener {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic channelTopic;

    public RedisPublishListener(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    public void publish(final Object message) {
        redisTemplate.convertAndSend(channelTopic.toString(), message);
        log.info("redis 向频道[{}]发布了[{}]消息", channelTopic.toString(), message.toString());
    }
}
