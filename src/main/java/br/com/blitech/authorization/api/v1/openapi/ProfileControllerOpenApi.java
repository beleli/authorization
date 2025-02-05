package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ProfileModel;
import br.com.blitech.authorization.api.v1.model.input.ProfileInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Profiles")
public interface ProfileControllerOpenApi {

    @PageableParameter
    @Operation(
            summary = "List all Profiles",
            responses = { @ApiResponse(responseCode = "200") }
    )
    Set<ProfileModel> findAll(@PathVariable Long applicationId) throws ApplicationNotFoundException;

    @Operation(
        summary = "Find Profile by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ProfileModel findById(@PathVariable Long applicationId, @PathVariable Long profileId) throws ProfileNotFoundException, ApplicationNotFoundException;

    @Operation(
        summary = "Insert a Profile",
        responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ProfileModel insert(@PathVariable Long applicationId, @NotNull @RequestBody ProfileInputModel profileInputModel) throws BusinessException;

    @Operation(
        summary = "Update Profile by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ProfileModel update(@PathVariable Long applicationId, @PathVariable Long profileId, @NotNull @RequestBody ProfileInputModel profileInputModel) throws BusinessException;

    @Operation(
        summary = "Delete Profile by Id",
        responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    void delete(@PathVariable Long applicationId, @PathVariable Long profileId) throws BusinessException;
}
