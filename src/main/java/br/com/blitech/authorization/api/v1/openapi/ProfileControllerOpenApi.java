package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ProfileModel;
import br.com.blitech.authorization.api.v1.model.input.ProfileInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Tag(name = "Profiles")
public interface ProfileControllerOpenApi {

    @PageableParameter
    @Operation(summary = "List all Profiles")
    Set<ProfileModel> findAll(@PathVariable Long applicationId) throws ApplicationNotFoundException;

    @Operation(summary = "Find Profile by Id")
    ProfileModel findById(@PathVariable Long applicationId, @PathVariable Long profileId) throws ProfileNotFoundException, ApplicationNotFoundException;

    @Operation(summary = "Insert a Profile")
    ProfileModel insert(@PathVariable Long applicationId, @NotNull @RequestBody ProfileInputModel profileInputModel) throws BusinessException;

    @Operation(summary = "Update Profile by Id")
    ProfileModel update(@PathVariable Long applicationId, @PathVariable Long profileId, @NotNull @RequestBody ProfileInputModel profileInputModel) throws BusinessException;

    @Operation(summary = "Delete Profile by Id")
    void delete(@PathVariable Long applicationId, @PathVariable Long profileId) throws BusinessException;
}
