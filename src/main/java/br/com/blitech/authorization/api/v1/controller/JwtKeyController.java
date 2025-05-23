package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimitAspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.JwksModel;
import br.com.blitech.authorization.api.v1.openapi.JwtKeyControllerOpenApi;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationKeyNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationKeyService;
import br.com.blitech.authorization.domain.service.ApplicationService;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@RestController
@RequestMapping("/.well-known")
public class JwtKeyController implements JwtKeyControllerOpenApi {
    private final ApplicationService applicationService;
    private final ApplicationKeyService applicationKeyService;
    private final JwtKeyProvider jwtKeyProvider;

    @Autowired
    public JwtKeyController(ApplicationService applicationService, ApplicationKeyService applicationKeyService, JwtKeyProvider jwtKeyProvider) {
        this.applicationService = applicationService;
        this.jwtKeyProvider = jwtKeyProvider;
        this.applicationKeyService = applicationKeyService;
    }

    @Override
    @RateLimit
    @LogAndValidate
    @GetMapping("/{applicationId}/jwks.json")
    public JwksModel getApplicationJwks(@PathVariable Long applicationId) throws ApplicationNotFoundException, ApplicationKeyNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        var application = applicationService.findOrThrow(applicationId);

        RSAPublicKey publicKey;
        Long kId;

        if (application.getUseDefaultKey()) {
            publicKey = (RSAPublicKey) jwtKeyProvider.getPublicKey();
            kId = jwtKeyProvider.getKeyId();
        }  else {
            var key = applicationKeyService.getLastPublicKey(application);
            kId = key.getFirst();
            publicKey = (RSAPublicKey) key.getSecond();
        }

        return createJwksModel(kId, publicKey);
    }

    @NotNull
    private JwksModel createJwksModel(Long kId, RSAPublicKey publicKey) {
        return new JwksModel(
            SignatureAlgorithm.RS256.getFamilyName(),
            kId.toString(),
            SignatureAlgorithm.RS256.getValue(),
            encodeBase64Url(publicKey.getModulus()),
            encodeBase64Url(publicKey.getPublicExponent())
        );
    }

    private String encodeBase64Url(@NotNull BigInteger value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray());
    }
}
