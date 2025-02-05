package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "JwtKey")
public interface JwtKeyControllerOpenApi {

   @Operation(
      summary = "Get JWT public key",
      responses = {
          @ApiResponse(responseCode = "200"),
          @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
          @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
      }
   )
   Map<String, Object> getJwks();
}
