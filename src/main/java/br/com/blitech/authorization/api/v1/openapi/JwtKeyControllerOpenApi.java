package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.v1.model.JwksModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "JwtKey")
public interface JwtKeyControllerOpenApi {

   @Operation(summary = "Get JWT public key")
   JwksModel getJwks();
}
