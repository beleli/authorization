package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.aspect.RateLimit;
import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.LoginUserInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
import br.com.blitech.authorization.domain.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Tag(name = "Login")
public interface LoginControllerOpenApi {

    @Operation(
        summary = "Generate access token with Application credentials",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    LoginModel loginApplication(@NotNull @RequestBody LoginApplicationInputModel loginApplicationInputModel) throws UserNotAuthorizedException;

    @Operation(
            summary = "Generate access token with User credentials",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
            }
    )
    LoginModel loginUser(@NotNull @RequestBody LoginUserInputModel loginUserInputModel) throws BusinessException;
}
