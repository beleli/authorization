package br.com.blitech.authorization.infrastructure.cache;

import br.com.blitech.authorization.domain.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

public class RedisAdapter implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisAdapter(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T getKey(String key, Class<T> clazz) {
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        Object value = valueOps.get(key);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return (T) value;
        }
        try {
            return objectMapper.readValue(value.toString(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize value for key: " + key, e);
        }
    }

    @Override
    public void setKey(String key, Object value, Duration duration) {
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        try {
            valueOps.set(key, objectMapper.writeValueAsString(value), duration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize value for key: " + key, e);
        }
    }
}
