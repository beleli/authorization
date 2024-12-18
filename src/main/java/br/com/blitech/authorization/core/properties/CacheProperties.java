package br.com.blitech.authorization.core.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authorization.cache")
public class CacheProperties {

    private Type type;
    private Redis redis;

    @PostConstruct
    public void validate() {
        if (type == Type.REDIS) {
            if (redis == null) {
                throw new IllegalArgumentException("blifood.cache.redis configuration cannot be null");
            }
        }
    }

    public Type getType() { return type; }
    public Redis getRedis() { return redis; }

    public void setType(Type type) { this.type = type; }
    public void setRedis(Redis redis) { this.redis = redis; }

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
