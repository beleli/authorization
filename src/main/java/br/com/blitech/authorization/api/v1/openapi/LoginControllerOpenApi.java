package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.LoginUserInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Login")
public interface LoginControllerOpenApi {

    @Operation(summary = "Generate access token with Application credentials")
    LoginModel loginApplication(@NotNull @RequestBody LoginApplicationInputModel loginApplicationInputModel) throws UserNotAuthorizedException;

    @Operation(summary = "Generate access token with Service User credentials")
    LoginModel loginServiceUser(@NotNull @RequestBody LoginUserInputModel loginUserInputModel) throws BusinessException;

    @Operation(summary = "Generate access token with User credentials")
    LoginModel loginUser(@NotNull @RequestBody LoginUserInputModel loginUserInputModel) throws BusinessException;
}
