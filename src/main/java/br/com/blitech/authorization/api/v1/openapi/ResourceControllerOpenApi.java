package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ResourceModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceInputModel;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ResourceAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ResourceInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Resources")
public interface ResourceControllerOpenApi {

    @PageableParameter
    @Operation(summary = "List all Resources")
    Page<ResourceModel> findAll(@PageableDefault() Pageable pageable);

    @Operation(summary = "Find Resource by Id")
    ResourceModel findById(@PathVariable Long resourceId) throws ResourceNotFoundException;

    @Operation(summary = "Insert a Resource")
    ResourceModel insert(@NotNull @RequestBody ResourceInputModel resourceInputModel) throws ResourceAlreadyExistsException;

    @Operation(summary = "Update Resource by Id")
    ResourceModel update(@PathVariable Long resourceId, @NotNull @RequestBody ResourceInputModel resourceInputModel) throws ResourceAlreadyExistsException, ResourceNotFoundException;

    @Operation(summary = "Delete Resource by Id")
    void delete(@PathVariable Long resourceId) throws ResourceNotFoundException, ResourceInUseException;
}
