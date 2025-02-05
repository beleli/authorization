package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ApplicationModelAssembler;
import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Applications")
public interface ApplicationControllerOpenApi {

    @PageableParameter
    @Operation(
        summary = "List all Applications",
        responses = { @ApiResponse(responseCode = "200") }
    )
    Page<ApplicationModel> findAll(@PageableDefault() Pageable pageable);

    @Operation(
        summary = "Find Application by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ApplicationModel findById(@PathVariable Long applicationId) throws ApplicationNotFoundException;

    @Operation(
        summary = "Insert a Application",
        responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ApplicationModel insert(@NotNull @RequestBody ApplicationInputModel applicationInputModel) throws ApplicationAlreadyExistsException;

    @Operation(
        summary = "Update Application by Id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    ApplicationModel update(@PathVariable Long applicationId, @NotNull @RequestBody ApplicationInputModel applicationInputModel) throws ApplicationAlreadyExistsException, ApplicationNotFoundException;

    @Operation(
        summary = "Delete Application by Id",
        responses = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(ref = "ApiError"))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(ref = "ApiError")))
        }
    )
    void delete(@PathVariable Long applicationId) throws ApplicationNotFoundException, ApplicationInUseException;
}
