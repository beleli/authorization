package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.*;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ServiceUserAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ServiceUserInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.*;
import br.com.blitech.authorization.domain.repository.ServiceUserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ServiceUserService {
    private final ApplicationService applicationService;
    private final ProfileService profileService;
    private final ServiceUserRepository serviceUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ServiceUserService(ApplicationService applicationService, ProfileService profileService, ServiceUserRepository serviceUserRepository, PasswordEncoder passwordEncoder) {
        this.applicationService = applicationService;
        this.profileService = profileService;
        this.serviceUserRepository = serviceUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Set<ServiceUser> findAll(Long applicationId) throws ApplicationNotFoundException {
        var application = applicationService.findOrThrow(applicationId);
        return serviceUserRepository.findByApplicationId(application.getId());
    }

    @Transactional(readOnly = true)
    public ServiceUser findOrThrow(Long id, Long applicationId) throws ApplicationNotFoundException, ServiceUserNotFoundException {
        var application = applicationService.findOrThrow(applicationId);
        return this.findOrThrow(id, application);
    }

    @Transactional(readOnly = true)
    public ServiceUser findOrThrow(Long id, @NotNull Application application) throws ApplicationNotFoundException, ServiceUserNotFoundException {
        return serviceUserRepository.findByIdAndApplicationId(id, application.getId()).orElseThrow(ServiceUserNotFoundException::new);
    }

    @Transactional(rollbackFor = {ServiceUserAlreadyExistsException.class, EntityNotFoundException.class})
    public ServiceUser save(@NotNull ServiceUser serviceUser) throws ServiceUserAlreadyExistsException, ServiceUserNotFoundException, ApplicationNotFoundException, ProfileNotFoundException {
        try {
            var application = applicationService.findOrThrow(serviceUser.getApplication().getId());
            if (serviceUser.getProfile() != null) profileService.findOrThrow(serviceUser.getProfile().getId(), application);
            serviceUser = serviceUserRepository.save(serviceUser);
            serviceUserRepository.flush();
            return serviceUser;
        } catch (DataIntegrityViolationException e) {
            throw new ServiceUserAlreadyExistsException();
        } catch (JpaObjectRetrievalFailureException e) {
            if (e.getMessage().contains(Application.class.getCanonicalName()))
                throw new ApplicationNotFoundException();
            if (e.getMessage().contains(Profile.class.getCanonicalName()))
                throw new ProfileNotFoundException();
            if (e.getMessage().contains(ServiceUser.class.getCanonicalName()))
                throw new ServiceUserNotFoundException();
            throw e;
        }
    }

    @Transactional(rollbackFor = ServiceUserInUseException.class)
    public void delete(Long applicationId, Long id) throws ApplicationNotFoundException, ServiceUserNotFoundException, ServiceUserInUseException {
        try {
            var application = applicationService.findOrThrow(applicationId);
            serviceUserRepository.delete(this.findOrThrow(id, application));
            serviceUserRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ServiceUserInUseException();
        }
    }

    @Transactional(readOnly = true)
    public ServiceUser validateLogin(String applicationName, String user, String password) throws ApplicationNotFoundException, UserInvalidPasswordException {
        var application = applicationService.findByNameOrThrow(applicationName);
        var serviceUser = serviceUserRepository.findByApplicationIdAndName(application.getId(), user).orElseThrow(UserInvalidPasswordException::new);
        if (!passwordEncoder.matches(password, serviceUser.getPassword())) throw new UserInvalidPasswordException();
        return serviceUser;
    }

    @Transactional(readOnly = true)
    public Set<String> getAuthorities(Long applicationId, Long id) throws ApplicationNotFoundException, ServiceUserNotFoundException, ProfileNotFoundException {
        var serviceUser = this.findOrThrow(applicationId, id);
        Set<String> authorities;

        if (serviceUser.getProfile() == null) {
            authorities = applicationService.getAuthorities(applicationId);
        } else {
            authorities = profileService.getAuthorities(serviceUser.getProfile().getId());
        }

        return authorities;
    }
}
