package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ActionModelAssembler;
import br.com.blitech.authorization.api.v1.model.ActionModel;
import br.com.blitech.authorization.api.v1.model.input.ActionInputModel;
import br.com.blitech.authorization.api.v1.openapi.ActionControllerOpenApi;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ActionAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ActionInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import br.com.blitech.authorization.domain.service.ActionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/v1/actions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionController implements ActionControllerOpenApi {

    @Autowired
    private ActionService actionService;

    @Autowired
    private ActionModelAssembler actionModelAssembler;

    @Override
    @GetMapping()
    @LogAndValidate(validateRequest = false, logResponse = false)
    @PreAuthorize("hasAuthority('ACTIONS.READ')")
    public Page<ActionModel> findAll(@PageableDefault() Pageable pageable) {
        return actionService.findAll(pageable).map(actionModelAssembler::toModel);
    }

    @Override
    @GetMapping("/{actionId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('ACTIONS.READ')")
    public ActionModel findById(@PathVariable Long actionId) throws ActionNotFoundException {
        return actionModelAssembler.toModel(actionService.findOrThrow(actionId));
    }

    @Override
    @PostMapping
    @LogAndValidate
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ACTIONS.WRITE')")
    public ActionModel insert(@NotNull @RequestBody ActionInputModel actionInputModel) throws ActionAlreadyExistsException {
        var action = actionService.save(actionModelAssembler.toEntity(actionInputModel));
        ResourceUriHelper.addUriInResponseHeader(action.getId());
        return actionModelAssembler.toModel(action);
    }

    @Override
    @PutMapping("/{actionId}")
    @LogAndValidate
    @PreAuthorize("hasAuthority('ACTIONS.WRITE')")
    public ActionModel update(@PathVariable Long actionId, @NotNull @RequestBody ActionInputModel actionInputModel) throws ActionAlreadyExistsException, ActionNotFoundException {
        var action = actionService.findOrThrow(actionId);
        var changedAction = actionService.save(actionModelAssembler.applyModel(action, actionInputModel));
        return actionModelAssembler.toModel(changedAction);
    }

    @Override
    @DeleteMapping("/{actionId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('ACTIONS.WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long actionId) throws ActionNotFoundException, ActionInUseException {
        actionService.delete(actionId);
    }
}
