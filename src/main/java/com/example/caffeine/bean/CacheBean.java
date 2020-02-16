package com.example.caffeine.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 缓存bean
 *
 * @author Alex
 */
@Data
public class CacheBean implements Serializable {

    private static final long serialVersionUID = -4242397072541977941L;

    private String cacheName;

    private long size;

    private int initialCapacity;

    private int maximumSize;

    private String survivalDuration;

    private long hitCount;

    private String hitRate;

    private long missCount;

    private String missRate;

    private long loadSuccessCount;

    private long loadFailureCount;

    private String loadFailureRate;

    // 单位秒
    private long totalLoadTime;

    private Date resetTime;

    private int highestSize;

    private Date highestTime;
}
