package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.v1.model.JwksModel;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationKeyNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Tag(name = "JwtKey")
public interface JwtKeyControllerOpenApi {

   @Operation(summary = "Get JWT public key for application")
   JwksModel getApplicationJwks(@PathVariable Long applicationId) throws ApplicationNotFoundException, ApplicationKeyNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException;
}
