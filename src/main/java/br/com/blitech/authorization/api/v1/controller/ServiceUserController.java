package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidateAspect.LogAndValidate;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ServiceUserModelAssembler;
import br.com.blitech.authorization.api.v1.model.ServiceUserModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserInputModel;
import br.com.blitech.authorization.api.v1.openapi.ServiceUserControllerOpenApi;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.entitynotfound.*;
import br.com.blitech.authorization.domain.service.ServiceUserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/v1/applications/{applicationId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceUserController implements ServiceUserControllerOpenApi {
    private final ServiceUserModelAssembler serviceUserModelAssembler;
    private final ServiceUserService serviceUserService;

    @Autowired
    public ServiceUserController(ServiceUserModelAssembler serviceUserModelAssembler, ServiceUserService serviceUserService) {
        this.serviceUserModelAssembler = serviceUserModelAssembler;
        this.serviceUserService = serviceUserService;
    }

    @Override
    @GetMapping()
    @LogAndValidate(validateRequest = false, logResponse = false)
    @PreAuthorize("hasAuthority('USERS.READ') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public Set<ServiceUserModel> findAll(@PathVariable Long applicationId) throws ApplicationNotFoundException {
        return serviceUserService.findAll(applicationId).stream().map(serviceUserModelAssembler::toModel).collect(Collectors.toSet());
    }

    @Override
    @GetMapping("/{userId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('USERS.READ') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ServiceUserModel findById(@PathVariable Long applicationId, @PathVariable Long userId) throws ServiceUserNotFoundException, ApplicationNotFoundException {
        return serviceUserModelAssembler.toModel(serviceUserService.findOrThrow(userId, applicationId));
    }

    @Override
    @PostMapping
    @LogAndValidate
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USERS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ServiceUserModel insert(@PathVariable Long applicationId, @NotNull @RequestBody ServiceUserInputModel serviceUserInputModel) throws BusinessException {
        try {
            var user = serviceUserService.save(serviceUserModelAssembler.toEntity(applicationId, serviceUserInputModel));
            ResourceUriHelper.addUriInResponseHeader(user.getId());
            return serviceUserModelAssembler.toModel(user);
        } catch (ApplicationNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @PutMapping("/{userId}")
    @LogAndValidate
    @PreAuthorize("hasAuthority('USERS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ServiceUserModel update(@PathVariable Long applicationId, @PathVariable Long userId, @NotNull @RequestBody ServiceUserInputModel serviceUserInputModel) throws BusinessException {
        try {
            var user = serviceUserService.findOrThrow(userId, applicationId);
            var changedUser = serviceUserService.save(serviceUserModelAssembler.applyModel(user, serviceUserInputModel));
            return serviceUserModelAssembler.toModel(changedUser);
        } catch (ApplicationNotFoundException | ProfileNotFoundException  e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{userId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('USERS.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long applicationId, @PathVariable Long userId) throws BusinessException {
        serviceUserService.delete(applicationId, userId);
    }
}
