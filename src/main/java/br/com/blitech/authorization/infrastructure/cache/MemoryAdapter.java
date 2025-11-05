package br.com.blitech.authorization.infrastructure.cache;

import br.com.blitech.authorization.domain.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryAdapter implements CacheService {
    private final ObjectMapper objectMapper;
    private final Map<String, Register> cache = new ConcurrentHashMap<>();

    @Autowired
    public MemoryAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T getKey(String key, Class<T> clazz) {
        cleanExpiredRegisters();
        Register register = cache.get(key);
        if (register == null) {
            return null;
        }
        try {
            return objectMapper.readValue(register.value(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize value for key: " + key, e);
        }
    }

    @Override
    public void setKey(String key, Object value, @NotNull Duration duration) {
        long expiration = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + duration.getSeconds();
        try {
            cache.put(key, new Register(objectMapper.writeValueAsString(value), expiration));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize value for key: " + key, e);
        }
    }

    private void cleanExpiredRegisters() {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        cache.entrySet().removeIf(entry -> entry.getValue().expiration() < now);
    }

    private record Register(String value, long expiration) { }
}
