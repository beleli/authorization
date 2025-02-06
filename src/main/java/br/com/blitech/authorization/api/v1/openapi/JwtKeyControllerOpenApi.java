package br.com.blitech.authorization.api.v1.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "JwtKey")
public interface JwtKeyControllerOpenApi {

   @Operation(summary = "Get JWT public key")
   Map<String, Object> getJwks();
}
