package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.v1.model.ServiceUserModel;
import br.com.blitech.authorization.api.v1.model.input.ChangePasswordInputModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserInputModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserPasswordInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ServiceUserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Tag(name = "Service Users")
public interface ServiceUserControllerOpenApi {

    @Operation(summary = "List all Service Users")
    Set<ServiceUserModel> findAll(@PathVariable Long applicationId) throws ApplicationNotFoundException;

    @Operation(summary = "Find Service User by Id")
    ServiceUserModel findById(@PathVariable Long applicationId, @PathVariable Long userId) throws ServiceUserNotFoundException, ApplicationNotFoundException;

    @Operation(summary = "Insert a Service User")
    ServiceUserModel insert(@PathVariable Long applicationId, @NotNull @RequestBody ServiceUserPasswordInputModel serviceUserPasswordInputModel) throws BusinessException;

    @Operation(summary = "Update Service User by Id")
    ServiceUserModel update(@PathVariable Long applicationId, @PathVariable Long userId, @NotNull @RequestBody ServiceUserInputModel serviceUserInputModel) throws BusinessException;

    @Operation(summary = "Delete Service User by Id")
    void delete(@PathVariable Long applicationId, @PathVariable Long userId) throws BusinessException;

    @Operation(summary = "Change Service User password")
    void changePassword(@PathVariable Long applicationId, @PathVariable Long userId, @NotNull @RequestBody ChangePasswordInputModel changePasswordInputModel) throws ApplicationNotFoundException, ServiceUserNotFoundException, UserInvalidPasswordException;
}
