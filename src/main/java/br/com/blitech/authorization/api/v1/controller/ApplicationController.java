package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ApplicationModelAssembler;
import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationPasswordInputModel;
import br.com.blitech.authorization.api.v1.model.input.ChangePasswordInputModel;
import br.com.blitech.authorization.api.v1.openapi.ApplicationControllerOpenApi;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationKeyService;
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

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(path = "/v1/applications", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApplicationController implements ApplicationControllerOpenApi {
    private final ApplicationModelAssembler applicationModelAssembler;
    private final ApplicationService applicationService;
    private final ApplicationKeyService applicationKeyService;

    @Autowired
    public ApplicationController(ApplicationModelAssembler applicationModelAssembler, ApplicationService applicationService, ApplicationKeyService applicationKeyService) {
        this.applicationModelAssembler = applicationModelAssembler;
        this.applicationService = applicationService;
        this.applicationKeyService = applicationKeyService;
    }

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
    public ApplicationModel insert(@NotNull @RequestBody ApplicationPasswordInputModel applicationPasswordInputModel) throws ApplicationAlreadyExistsException, NoSuchAlgorithmException {
        var application = applicationService.save(applicationModelAssembler.toEntity(applicationPasswordInputModel));
        ResourceUriHelper.addUriInResponseHeader(application.getId());
        return applicationModelAssembler.toModel(application);
    }

    @Override
    @PutMapping("/{applicationId}")
    @LogAndValidate
    @PreAuthorize("hasAuthority('APPLICATIONS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ApplicationModel update(@PathVariable Long applicationId, @NotNull @RequestBody ApplicationInputModel applicationInputModel) throws ApplicationAlreadyExistsException, ApplicationNotFoundException, NoSuchAlgorithmException {
        var application = applicationService.findOrThrow(applicationId);
        var changedApplication = applicationService.save(applicationModelAssembler.applyModel(application, applicationInputModel));
        return applicationModelAssembler.toModel(changedApplication);
    }

    @Override
    @DeleteMapping("/{applicationId}")
    @LogAndValidate(validateRequest = false)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('APPLICATIONS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public void delete(@PathVariable Long applicationId) throws ApplicationNotFoundException, ApplicationInUseException {
        applicationService.delete(applicationId);
    }

    @Override
    @PutMapping("/{applicationId}/password")
    @LogAndValidate(logResponse = false)
    @PreAuthorize("hasAuthority('APPLICATIONS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public void changePassword(@PathVariable Long applicationId, @NotNull @RequestBody ChangePasswordInputModel changePasswordInputModel) throws ApplicationNotFoundException, UserInvalidPasswordException {
        var application = applicationService.findOrThrow(applicationId);
        applicationService.changePassword(application, changePasswordInputModel.getPassword(), changePasswordInputModel.getNewPassword());
    }

    @Override
    @PostMapping("/{applicationId}/keys")
    @LogAndValidate(validateRequest = false, logResponse = false)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('KEYS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public void insertKey(@PathVariable Long applicationId) throws ApplicationNotFoundException, NoSuchAlgorithmException {
        var application = applicationService.findOrThrow(applicationId);
        applicationKeyService.save(application);
    }
}
