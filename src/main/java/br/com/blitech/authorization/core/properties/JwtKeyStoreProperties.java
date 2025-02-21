package br.com.blitech.authorization.core.properties;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.IOException;

@ConfigurationProperties("authorization.jwt-keys")
public class JwtKeyStoreProperties {

    private Resource publicKey;
    private Resource privateKey;

    public Resource getPublicKey() { return publicKey; }
    public Resource getPrivateKey() { return privateKey; }

    public JwtKeyStoreProperties(@NotNull Resource publicKey, @NotNull Resource privateKey) throws IOException {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
