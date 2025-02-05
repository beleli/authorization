package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.input.ActionInputModel;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ActionAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ActionInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Actions")
public interface ActionControllerOpenApi {

    @PageableParameter
    @Operation(
        summary = "List all Actions",
        responses = { @ApiResponse(responseCode = "200") }
    )
    Page<ActionModel> findAll(@PageableDefault() Pageable pageable);

    @Operation(
        summary = "Find Action by Id",
        responses = {

            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ActionModel findById(@PathVariable Long actionId) throws ActionNotFoundException;

    @Operation(
        summary = "Insert a City",
        responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ActionModel insert(@NotNull @RequestBody ActionInputModel actionInputModel) throws ActionAlreadyExistsException;

    @Operation(
        summary = "Update Action by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ActionModel update(@PathVariable Long actionId, @NotNull @RequestBody ActionInputModel actionInputModel) throws ActionAlreadyExistsException, ActionNotFoundException;

    @Operation(
        summary = "Delete Action by Id",
        responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    void delete(@PathVariable Long actionId) throws ActionNotFoundException, ActionInUseException;
}
