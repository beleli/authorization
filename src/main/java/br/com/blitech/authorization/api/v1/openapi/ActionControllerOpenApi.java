package br.com.blitech.authorization.api.v1.openapi;

import br.com.blitech.authorization.api.openapi.PageableParameter;
import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.input.ActionInputModel;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ActionAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ActionInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "List all Actions")
    Page<ActionModel> findAll(@PageableDefault() Pageable pageable);

    @Operation(summary = "Find Action by Id")
    ActionModel findById(@PathVariable Long actionId) throws ActionNotFoundException;

    @Operation(summary = "Insert a City")
    ActionModel insert(@NotNull @RequestBody ActionInputModel actionInputModel) throws ActionAlreadyExistsException;

    @Operation(summary = "Update Action by Id")
    ActionModel update(@PathVariable Long actionId, @NotNull @RequestBody ActionInputModel actionInputModel) throws ActionAlreadyExistsException, ActionNotFoundException;

    @Operation(summary = "Delete Action by Id")
    void delete(@PathVariable Long actionId) throws ActionNotFoundException, ActionInUseException;
}
