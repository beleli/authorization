package br.com.blitech.authorization.core.io;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class StringProtocolResolver implements ProtocolResolver, ApplicationListener<ApplicationContextInitializedEvent> {
    private static final String STRING_PREFIX = "string:";

    @Override
    public Resource resolve(@NotNull String location, ResourceLoader resourceLoader) {
        if (location.startsWith(STRING_PREFIX)) {
            String decodedResource = location.substring(STRING_PREFIX.length());
            return new ByteArrayResource(decodedResource.getBytes());
        }
        return null;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationContextInitializedEvent event) {
        event.getApplicationContext().addProtocolResolver(this);
    }
}
