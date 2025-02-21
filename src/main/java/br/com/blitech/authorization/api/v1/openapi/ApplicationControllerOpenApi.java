package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationPasswordInputModel;
import br.com.blitech.authorization.api.v1.model.input.ChangePasswordInputModel;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.NoSuchAlgorithmException;

@Tag(name = "Applications")
public interface ApplicationControllerOpenApi {

    @PageableParameter
    @Operation(summary = "List all Applications")
    Page<ApplicationModel> findAll(@PageableDefault() Pageable pageable);

    @Operation(summary = "Find Application by Id")
    ApplicationModel findById(@PathVariable Long applicationId) throws ApplicationNotFoundException;

    @Operation(summary = "Insert a Application")
    ApplicationModel insert(@NotNull @RequestBody ApplicationPasswordInputModel applicationPasswordInputModel) throws ApplicationAlreadyExistsException, NoSuchAlgorithmException;

    @Operation(summary = "Update Application by Id")
    ApplicationModel update(@PathVariable Long applicationId, @NotNull @RequestBody ApplicationInputModel applicationInputModel) throws ApplicationAlreadyExistsException, ApplicationNotFoundException, NoSuchAlgorithmException;

    @Operation(summary = "Delete Application by Id")
    void delete(@PathVariable Long applicationId) throws ApplicationNotFoundException, ApplicationInUseException;

    @Operation(summary = "Change User Application password")
    void changePassword(@PathVariable Long applicationId, @NotNull @RequestBody ChangePasswordInputModel changePasswordInputModel) throws ApplicationNotFoundException, UserInvalidPasswordException;

    @Operation(summary = "Create a new encryption key for the application")
    void insertKey(@PathVariable Long applicationId) throws ApplicationNotFoundException, NoSuchAlgorithmException;
}
