package com.king.lms.e_learning_hub.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisUtils {

    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    public <T> void set(String key,T date, long timeoutSeconds){

        try {
            String json = objectMapper.writeValueAsString(date);
            redisTemplate.opsForValue().set(key, json,timeoutSeconds);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public <T> T get (String key, Class<T> clazz){

        String json = redisTemplate.opsForValue().get(key);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

    }

    public long getTTL(String key){
        try {
            Long ttl = redisTemplate.getExpire(key,TimeUnit.SECONDS);

            return ttl != null?ttl:-2;
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean delete(String key){
        if(key != null)
            return redisTemplate.delete(key);

        return false;
    }

}
