package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ResourceModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceInputModel;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ResourceAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ResourceInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Resources")
public interface ResourceControllerOpenApi {

    @PageableParameter
    @Operation(
        summary = "List all Resources",
        responses = { @ApiResponse(responseCode = "200") }
    )
    Page<ResourceModel> findAll(@PageableDefault() Pageable pageable);

    @Operation(
        summary = "Find Resource by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ResourceModel findById(@PathVariable Long resourceId) throws ResourceNotFoundException;

    @Operation(
        summary = "Insert a Resource",
        responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ResourceModel insert(@NotNull @RequestBody ResourceInputModel resourceInputModel) throws ResourceAlreadyExistsException;

    @Operation(
        summary = "Update Resource by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ResourceModel update(@PathVariable Long resourceId, @NotNull @RequestBody ResourceInputModel resourceInputModel) throws ResourceAlreadyExistsException, ResourceNotFoundException;

    @Operation(
        summary = "Delete Resource by Id",
        responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    void delete(@PathVariable Long resourceId) throws ResourceNotFoundException, ResourceInUseException;
}
