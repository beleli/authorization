package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ProfileAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ProfileInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import br.com.blitech.authorization.domain.repository.ProfileRepository;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ProfileService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileResourceActionRepository profileResourceActionRepository;

    @Transactional(readOnly = true)
    public Set<Profile> findAll(Long applicationId) throws ApplicationNotFoundException {
        return applicationService.findOrThrow(applicationId).getProfiles();
    }

    @Transactional(readOnly = true)
    public Profile findOrThrow(Long id, Long applicationId) throws ApplicationNotFoundException, ProfileNotFoundException {
        var application = applicationService.findOrThrow(applicationId);
        return findOrThrow(id, application);
    }

    @Transactional(readOnly = true)
    public Profile findOrThrow(Long id, @NotNull Application application) throws ApplicationNotFoundException, ProfileNotFoundException {
        return profileRepository.findByIdAndApplicationId(id, application.getId()).orElseThrow(ProfileNotFoundException::new);
    }

    @Transactional(rollbackFor = ProfileAlreadyExistsException.class)
    public Profile save(@NotNull Profile profile) throws ProfileAlreadyExistsException, ApplicationNotFoundException {
        try {
            applicationService.findOrThrow(profile.getApplication().getId());
            profile = profileRepository.save(profile);
            profileRepository.flush();
            return profile;
        } catch (DataIntegrityViolationException e) {
            throw new ProfileAlreadyExistsException();
        }
    }

    @Transactional(rollbackFor = ProfileInUseException.class)
    public void delete(Long applicationId, Long id) throws ApplicationNotFoundException, ProfileNotFoundException, ProfileInUseException {
        try {
            var application = applicationService.findOrThrow(applicationId);
            profileRepository.delete(findOrThrow(id, application));
            profileRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ProfileInUseException();
        }
    }

    @Transactional(readOnly = true)
    public Set<String> getProfileAuthorities(String name) throws ProfileNotFoundException {
        Optional<Profile> profile = profileRepository.findByName(name);
        if (profile.isEmpty()) throw new ProfileNotFoundException();

        Set<String> authorities = new HashSet<>();
        for (ProfileResourceAction profileResourceAction : profileResourceActionRepository.findByProfile(profile.get())) {
            authorities.add(profileResourceAction.getAuthority());
        }

        return authorities;
    }
}
