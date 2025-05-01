package com.mild.andyou.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperUtils {

    private ObjectMapperUtils() {
        // 외부에서 인스턴스 생성 방지
    }

    private static class Holder {
        private static final ObjectMapper INSTANCE = createObjectMapper();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static ObjectMapper getInstance() {
        return Holder.INSTANCE;
    }

    public static <T> T getFromJson(String json, Class<T> clazz) {
        if (json == null) return null;

        try {
            return Holder.INSTANCE.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 조회 실패: JSON 역직렬화 오류", e);
        }
    }

    public static <T> T getFromJson(String json, TypeReference<T> typeRef) {
        if (json == null) return null;

        try {
            return getInstance().readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 역직렬화 실패", e);
        }
    }
    public static String getToJson(Object value) {
        try {
            return Holder.INSTANCE.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 실패: JSON 직렬화 오류", e);
        }
    }

}
