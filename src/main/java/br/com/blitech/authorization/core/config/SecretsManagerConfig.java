package br.com.blitech.authorization.core.config;

import br.com.blitech.authorization.core.properties.SecretsManagerProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(SecretsManagerProperties.class)
public class SecretsManagerConfig {
    private final SecretsManagerProperties secretsManagerProperties;

    @Autowired
    public SecretsManagerConfig(SecretsManagerProperties secretsManagerProperties) {
        this.secretsManagerProperties = secretsManagerProperties;
    }

    @NotNull
    public static SecretsManagerConfig createWithImpl(SecretsManagerProperties.Impl impl) {
        return new SecretsManagerConfig(new SecretsManagerProperties(impl));
    }

    @Bean
    public SecretsManagerClient secretsManager() {
        switch (secretsManagerProperties.getImpl()) {
            case AWS:
                return getAwsImpl();
            case LOCALSTACK:
                return getLocalstackImpl();
            default:
                throw new IllegalArgumentException("Unsupported implementation: " + secretsManagerProperties.getImpl());
        }
    }

    private SecretsManagerClient getAwsImpl() {
        return SecretsManagerClient.create();
    }

    private SecretsManagerClient getLocalstackImpl() {
        return SecretsManagerClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.of("us-east-1"))
                .credentialsProvider(() -> AwsBasicCredentials.create("localstack", "localstack"))
                .build();
    }
}
