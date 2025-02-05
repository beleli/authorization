package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ResourceModelAssembler;
import br.com.blitech.authorization.api.v1.model.ResourceModel;
import br.com.blitech.authorization.api.v1.model.input.ResourceInputModel;
import br.com.blitech.authorization.api.v1.openapi.ResourceControllerOpenApi;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ResourceAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ResourceInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import br.com.blitech.authorization.domain.service.ResourceService;
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
@RequestMapping(path = "/v1/resources", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourceController implements ResourceControllerOpenApi {

    @Autowired
    private ResourceModelAssembler resourceModelAssembler;

    @Autowired
    private ResourceService resourceService;

    @Override
    @GetMapping()
    @LogAndValidate(validateRequest = false, logResponse = false)
    @PreAuthorize("hasAuthority('RESOURCES.READ')")
    public Page<ResourceModel> findAll(@PageableDefault() Pageable pageable) {
        return resourceService.findAll(pageable).map(resourceModelAssembler::toModel);
    }

    @Override
    @GetMapping("/{resourceId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('RESOURCES.READ')")
    public ResourceModel findById(@PathVariable Long resourceId) throws ResourceNotFoundException {
        return resourceModelAssembler.toModel(resourceService.findOrThrow(resourceId));
    }

    @Override
    @PostMapping
    @LogAndValidate
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('RESOURCES.WRITE')")
    public ResourceModel insert(@NotNull @RequestBody ResourceInputModel resourceInputModel) throws ResourceAlreadyExistsException {
        var resource = resourceService.save(resourceModelAssembler.toEntity(resourceInputModel));
        ResourceUriHelper.addUriInResponseHeader(resource.getId());
        return resourceModelAssembler.toModel(resource);
    }

    @Override
    @PutMapping("/{resourceId}")
    @LogAndValidate
    @PreAuthorize("hasAuthority('RESOURCES.WRITE')")
    public ResourceModel update(@PathVariable Long resourceId, @NotNull @RequestBody ResourceInputModel resourceInputModel) throws ResourceAlreadyExistsException, ResourceNotFoundException {
        var resource = resourceService.findOrThrow(resourceId);
        var changedResource = resourceService.save(resourceModelAssembler.applyModel(resource, resourceInputModel));
        return resourceModelAssembler.toModel(changedResource);
    }

    @Override
    @DeleteMapping("/{resourceId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('RESOURCES.WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long resourceId) throws ResourceNotFoundException, ResourceInUseException {
        resourceService.delete(resourceId);
    }
}
