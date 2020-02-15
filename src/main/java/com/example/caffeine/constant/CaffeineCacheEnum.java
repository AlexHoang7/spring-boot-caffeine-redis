package com.example.caffeine.constant;

/**
 * caffeine缓存枚举
 *
 * @author Alex
 */
public enum CaffeineCacheEnum {

    /**
     * 用户
     */
    USER("user"),

    /**
     * 订单
     */
    ORDER("order"),

    /**
     * 预约单
     */
    APPOINTMENT("appointment"),

    /**
     * 购买人
     */
    PURCHASER("purchaser"),

    /**
     * 使用人
     */
    HOLDER("holder"),

    ;

    private String value;

    public String value() {
        return this.value;
    }

    CaffeineCacheEnum(String value) {
        this.value = value;
    }
}
