package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Tag(name = "Login")
public interface LoginControllerOpenApi {

    @Operation(summary = "Generate access token with Application credentials")
    LoginModel loginApplication(@NotNull @RequestBody LoginInputModel loginInputModel) throws UserNotAuthorizedException, BusinessException, NoSuchAlgorithmException, InvalidKeySpecException;

    @Operation(summary = "Generate access token with Service User credentials")
    LoginModel loginServiceUser(@NotNull @RequestBody LoginInputModel loginInputModel) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException;

    @Operation(summary = "Generate access token with User credentials")
    LoginModel loginUser(@NotNull @RequestBody LoginInputModel loginInputModel) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException;
}
