package br.com.blitech.authorization.api.security;

import br.com.blitech.authorization.core.properties.JwtKeyStoreProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;

@Component
@EnableConfigurationProperties(JwtKeyStoreProperties.class)
public class JwtKeyProvider {
    private final JwtKeyStoreProperties properties;
    private final KeyStore keyStore;
    private final Key key;
    private final PublicKey publicKey;

    public Key getKey() { return key; }
    public PublicKey getPublicKey() { return publicKey; }

    public JwtKeyProvider(JwtKeyStoreProperties properties) {
        this.properties = properties;
        this.keyStore = loadKeyStore();
        this.key = loadKey();
        this.publicKey = loadPublicKey();
    }

    @NotNull
    private KeyStore loadKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(
                    properties.getJksLocation().getInputStream(),
                    properties.getPassword().getContentAsString(Charset.defaultCharset()).toCharArray()
            );
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load KeyStore", e);
        }
    }

    private Key loadKey() {
        try {
            return keyStore.getKey(
                properties.getKeypairAlias().getContentAsString(Charset.defaultCharset()),
                properties.getPassword().getContentAsString(Charset.defaultCharset()).toCharArray()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Key from KeyStore", e);
        }
    }

    private PublicKey loadPublicKey() {
        try {
            return keyStore.getCertificate(
                properties.getKeypairAlias().getContentAsString(Charset.defaultCharset())
            ).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load PublicKey from KeyStore", e);
        }
    }
}
