package br.com.blitech.authorization.core.io;

import br.com.blitech.authorization.core.config.SecretsManagerConfig;
import br.com.blitech.authorization.core.properties.SecretsManagerProperties;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public class SecretsManagerProtocolResolver implements ProtocolResolver, ApplicationListener<ApplicationContextInitializedEvent> {
    private static final String SECRETS_MANAGER_PREFIX = "secretsmanager:";
    private final SecretsManagerClient secretsManagerClient = setSecretsManagerClient();
    private final Base64ProtocolResolver base64ProtocolResolver = new Base64ProtocolResolver();

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (location.startsWith(SECRETS_MANAGER_PREFIX)) {
            String key = location.substring(SECRETS_MANAGER_PREFIX.length());
            String decodedResource = this.getSecretValue(key);
            Resource resolvedResource = base64ProtocolResolver.resolve(decodedResource, resourceLoader);
            return resolvedResource != null ? resolvedResource : new ByteArrayResource(decodedResource.getBytes());
        }
        return null;
    }

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        event.getApplicationContext().addProtocolResolver(this);
    }

    private SecretsManagerClient setSecretsManagerClient() {
        String env = System.getenv("AUTHORIZATION_SECRETS_MANAGER_IMPL");
        SecretsManagerProperties.Impl impl = (env != null && !env.isEmpty())
                ? SecretsManagerProperties.Impl.valueOf(env.toUpperCase())
                : SecretsManagerProperties.Impl.AWS;
        return SecretsManagerConfig.createWithImpl(impl).secretsManager();
    }

    private String getSecretValue(String secretName) {
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        return secretsManagerClient.getSecretValue(valueRequest).secretString();
    }
}
