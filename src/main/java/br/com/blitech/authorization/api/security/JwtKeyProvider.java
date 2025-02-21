package br.com.blitech.authorization.api.security;

import br.com.blitech.authorization.core.properties.JwtKeyStoreProperties;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
@EnableConfigurationProperties(JwtKeyStoreProperties.class)
public class JwtKeyProvider {
    private final JwtKeyStoreProperties properties;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final Long keyId;

    @Autowired
    public JwtKeyProvider(@NotNull JwtKeyStoreProperties properties) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        this.properties = properties;
        this.publicKey = loadPublicKey();
        this.privateKey = loadPrivateKey();
        this.keyId = 0L;
    }

    public PublicKey getPublicKey() { return publicKey; }
    public PrivateKey getPrivateKey() { return privateKey; }
    public Long getKeyId() { return keyId; }

    private PublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(SignatureAlgorithm.RS256.getFamilyName());
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(properties.getPublicKey().getContentAsByteArray());

        return keyFactory.generatePublic(publicKeySpec);
    }

    private PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(SignatureAlgorithm.RS256.getFamilyName());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(properties.getPrivateKey().getContentAsByteArray());

        return keyFactory.generatePrivate(privateKeySpec);
    }
}
