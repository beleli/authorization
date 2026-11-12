package br.com.blitech.authorization.infrastructure.cache;

import br.com.blitech.authorization.domain.service.CacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

public class RedisAdapter implements CacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T getKey(String key, Class<T> clazz) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize value for key: " + key, e);
        }
    }

    @Override
    public void setKey(String key, Object value, Duration duration) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null for key: " + key);
        }
        String serializedValue;
        try {
            serializedValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize value for key: " + key, e);
        }
        redisTemplate.opsForValue().set(key, serializedValue, duration);
    }
}
