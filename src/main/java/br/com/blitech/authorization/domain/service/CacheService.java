package br.com.blitech.authorization.domain.service;

import java.time.Duration;

public interface CacheService {
    <T> T getKey(String key, Class<T> clazz);
    void setKey(String key, Object value, Duration duration);
}
