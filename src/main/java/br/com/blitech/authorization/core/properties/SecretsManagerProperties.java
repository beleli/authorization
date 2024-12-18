package br.com.blitech.authorization.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authorization.secrets-manager")
public class SecretsManagerProperties {

    private Impl impl;

    public SecretsManagerProperties(Impl impl) {
        this.impl = impl;
    }

    public Impl getImpl() {
        return impl;
    }

    public enum Impl {
        AWS,
        LOCALSTACK
    }
}
