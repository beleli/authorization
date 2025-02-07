package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimitAspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.JwksModel;
import br.com.blitech.authorization.api.v1.openapi.JwtKeyControllerOpenApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/.well-known")
public class JwtKeyController implements JwtKeyControllerOpenApi {
    private JwtKeyProvider jwtKeyProvider;

    @Autowired
    public JwtKeyController(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    @Override
    @RateLimit
    @LogAndValidate
    @GetMapping("/jwks.json")
    public JwksModel getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) jwtKeyProvider.getPublicKey();
        String kId = jwtKeyProvider.getKeyId();

        return new JwksModel(
                "RSA",
                kId,
                "RS256",
                "sig",
                encodeBase64Url(publicKey.getModulus()),
                encodeBase64Url(publicKey.getPublicExponent())
        );
    }

    private String encodeBase64Url(@NotNull BigInteger value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray());
    }
}
