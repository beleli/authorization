package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.aspect.LogAndValidate;
import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ProfileModelAssembler;
import br.com.blitech.authorization.api.v1.model.ProfileModel;
import br.com.blitech.authorization.api.v1.model.input.ProfileInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ProfileAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import br.com.blitech.authorization.domain.service.ProfileService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/v1/applications/{applicationId}/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

    @Autowired
    ProfileModelAssembler profileModelAssembler;

    @Autowired
    private ProfileService profileService;

    @GetMapping()
    @LogAndValidate(validateRequest = false, logResponse = false)
    @PreAuthorize("hasAuthority('PROFILES.READ') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public Set<ProfileModel> findAll(@PathVariable Long applicationId) throws ApplicationNotFoundException {
        return profileService.findAll(applicationId).stream().map(profileModelAssembler::toModel).collect(Collectors.toSet());
    }

    @GetMapping("/{profileId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('PROFILES.READ') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ProfileModel find(@PathVariable Long applicationId, @PathVariable Long profileId) throws ProfileNotFoundException, ApplicationNotFoundException {
        return profileModelAssembler.toModel(profileService.findOrThrow(profileId, applicationId));
    }

    @PostMapping
    @LogAndValidate
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PROFILES.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ProfileModel insert(@PathVariable Long applicationId, @NotNull @RequestBody ProfileInputModel profileInputModel) throws BusinessException {
        try {
            var profile = profileService.save(profileModelAssembler.toEntity(applicationId, profileInputModel));
            ResourceUriHelper.addUriInResponseHeader(profile.getId());
            return profileModelAssembler.toModel(profile);
        } catch (ApplicationNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/{profileId}")
    @LogAndValidate
    @PreAuthorize("hasAuthority('PROFILES.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    public ProfileModel update(@PathVariable Long applicationId, @PathVariable Long profileId, @NotNull @RequestBody ProfileInputModel profileInputModel) throws BusinessException {
        try {
            var profile = profileService.findOrThrow(profileId, applicationId);
            var changedProfile = profileService.save(profileModelAssembler.applyModel(applicationId, profile, profileInputModel));
            return profileModelAssembler.toModel(changedProfile);
        } catch (ApplicationNotFoundException | ResourceNotFoundException | ActionNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @DeleteMapping("/{profileId}")
    @LogAndValidate(validateRequest = false)
    @PreAuthorize("hasAuthority('PROFILES.WRITE') or @resourceUriHelper.isYourselfApplication(#applicationId)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long applicationId, @PathVariable Long profileId) throws BusinessException {
        profileService.delete(applicationId, profileId);
    }
}
