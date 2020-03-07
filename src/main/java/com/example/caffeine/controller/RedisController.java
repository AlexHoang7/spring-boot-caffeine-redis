package com.example.caffeine.controller;

import com.example.caffeine.configuration.RedisPublishListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis controller
 *
 * @author Alex
 */
@Slf4j
@RestController
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/publish")
    public void publish(String message) {
        RedisPublishListener redisPublishListener = new RedisPublishListener(redisTemplate, new ChannelTopic("redis:test:topic"));
        redisPublishListener.publish(message);
    }
}
