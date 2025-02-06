package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimitAspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.openapi.JwtKeyControllerOpenApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwtKeyController implements JwtKeyControllerOpenApi {

    @Autowired
    private JwtKeyProvider jwtKeyProvider;

    @Override
    @RateLimit
    @LogAndValidate
    @GetMapping("/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) jwtKeyProvider.getPublicKey();
        String kId = jwtKeyProvider.getKeyId();

        Map<String, Object> jwk = Map.of(
                "kty", "RSA",
                "kid", kId, // Defina um ID único para a chave
                "alg", "RS256",
                "use", "sig",
                "n", encodeBase64Url(publicKey.getModulus()),
                "e", encodeBase64Url(publicKey.getPublicExponent())
        );

        return Collections.singletonMap("keys", Collections.singletonList(jwk));
    }

    private String encodeBase64Url(@NotNull BigInteger value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray());
    }
}
