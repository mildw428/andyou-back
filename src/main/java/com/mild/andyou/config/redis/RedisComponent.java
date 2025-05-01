package com.mild.andyou.config.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mild.andyou.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisComponent {
    private final StringRedisTemplate redisTemplate;

    public <T> T  get(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        return ObjectMapperUtils.getFromJson(json, clazz);
    }

    public <T> T  get(String key, TypeReference<T> typeRef) {
        String json = redisTemplate.opsForValue().get(key);
        return ObjectMapperUtils.getFromJson(json, typeRef);
    }

    public void save(String key, Object content, Long seconds) {
        try {
            redisTemplate.opsForValue().set(key, ObjectMapperUtils.getToJson(content));
            redisTemplate.expire(key, Duration.ofSeconds(seconds));
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void delete(String id) {
        String key = RedisKey.getKey(id);
        redisTemplate.delete(key);
    }

}