package br.com.blitech.authorization.core.config;

import br.com.blitech.authorization.core.properties.CacheProperties;
import br.com.blitech.authorization.domain.service.CacheService;
import br.com.blitech.authorization.infrastructure.cache.MemoryAdapter;
import br.com.blitech.authorization.infrastructure.cache.RedisAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {
    private final CacheProperties cacheProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheConfig(CacheProperties cacheProperties, ObjectMapper objectMapper) {
        this.cacheProperties = cacheProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public CacheService cacheService() {
        if (cacheProperties.getType() == CacheProperties.Type.REDIS) {
            return new RedisAdapter(redisTemplate(redisConnectionFactory()), objectMapper);
        } else if (cacheProperties.getType() == CacheProperties.Type.MEMORY) {
            return new MemoryAdapter(objectMapper);
        } else {
            throw new IllegalStateException("Unsupported cache type: " + cacheProperties.getType());
        }
    }

    @Bean
    @ConditionalOnProperty(name = "authorization.cache.type", havingValue = "redis")
    public RedisConnectionFactory redisConnectionFactory() {
        CacheProperties.Redis redis = cacheProperties.getRedis();
        if (redis == null) {
            throw new IllegalArgumentException("Redis configuration cannot be null for type REDIS");
        }
        return new LettuceConnectionFactory(redis.getHost(), redis.getPort());
    }

    @Bean
    @ConditionalOnProperty(name = "authorization.cache.type", havingValue = "redis")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
