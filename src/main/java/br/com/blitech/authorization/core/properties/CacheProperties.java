package br.com.blitech.authorization.core.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authorization.cache")
public class CacheProperties {
    private Type type;
    private Redis redis;

    public CacheProperties(Type type, Redis redis) {
        this.type = type;
        this.redis = redis;
    }

    @PostConstruct
    public void validate() {
        if (type == Type.REDIS && redis == null) {
            throw new IllegalArgumentException("blifood.cache.redis configuration cannot be null");
        }
    }

    public Type getType() { return type; }
    public Redis getRedis() { return redis; }

    public enum Type {
        REDIS,
        MEMORY
    }

    public static class Redis {
        private String host;
        private int port;

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }
}
