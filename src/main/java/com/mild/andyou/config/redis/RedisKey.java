package com.mild.andyou.config.redis;

public class RedisKey {

    private static final String KEY_NAMESPACE = "com:example:redis:test";

    public static String getKey(String userId) {
        return String.join(":", KEY_NAMESPACE, userId);
    }

}