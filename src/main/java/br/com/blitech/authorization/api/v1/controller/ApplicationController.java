package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ApplicationModelAssembler;
import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.api.v1.openapi.ApplicationControllerOpenApi;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
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
@RequestMapping(path = "/v1/applications", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApplicationController implements ApplicationControllerOpenApi {

    @Autowired
    private ApplicationModelAssembler applicationModelAssembler;

    @Autowired
    private ApplicationService applicationService;

    @Override
    @GetMapping
    @LogAndValidate(validateRequest = false, logResponse = false)
    @PreAuthorize("hasAuthority('APPLICATIONS.READ')")
    public Page<ApplicationModel> findAll(@PageableDefault() Pageable pageable) {
        return applicationService.findAll(pageable).map(applicationModelAssembler::toModel);
    }

    @Override
    @GetMapping("/{applicationId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('APPLICATIONS.READ') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ApplicationModel findById(@PathVariable Long applicationId) throws ApplicationNotFoundException {
        return applicationModelAssembler.toModel(applicationService.findOrThrow(applicationId));
    }

    @Override
    @PostMapping
    @LogAndValidate
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('APPLICATIONS.WRITE')")
    public ApplicationModel insert(@NotNull @RequestBody ApplicationInputModel applicationInputModel) throws ApplicationAlreadyExistsException {
        var application = applicationService.save(applicationModelAssembler.toEntity(applicationInputModel));
        ResourceUriHelper.addUriInResponseHeader(application.getId());
        return applicationModelAssembler.toModel(application);
    }

    @Override
    @PutMapping("/{applicationId}")
    @LogAndValidate
    @PreAuthorize("hasAuthority('APPLICATIONS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ApplicationModel update(@PathVariable Long applicationId, @NotNull @RequestBody ApplicationInputModel applicationInputModel) throws ApplicationAlreadyExistsException, ApplicationNotFoundException {
        var application = applicationService.findOrThrow(applicationId);
        var changedApplication = applicationService.save(applicationModelAssembler.applyModel(application, applicationInputModel));
        return applicationModelAssembler.toModel(changedApplication);
    }

    @Override
    @DeleteMapping("/{applicationId} or @resourceUriHelper.isYourselfApplication(#applicationId)")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('APPLICATIONS.WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long applicationId) throws ApplicationNotFoundException, ApplicationInUseException {
        applicationService.delete(applicationId);
    }
}
