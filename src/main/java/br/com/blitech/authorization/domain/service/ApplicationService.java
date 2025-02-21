package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.repository.ApplicationRepository;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;

@Service
public class ApplicationService {
    private final ApplicationKeyService applicationKeyService;
    private final ApplicationRepository applicationRepository;
    private final ProfileResourceActionRepository profileResourceActionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationService(ApplicationKeyService applicationKeyService, ApplicationRepository applicationRepository, ProfileResourceActionRepository profileResourceActionRepository, PasswordEncoder passwordEncoder) {
        this.applicationKeyService = applicationKeyService;
        this.applicationRepository = applicationRepository;
        this.profileResourceActionRepository = profileResourceActionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Application findOrThrow(Long id) throws ApplicationNotFoundException {
        return this.internalFindOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Page<Application> findAll(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Application findByNameOrThrow(String name) throws ApplicationNotFoundException {
        return applicationRepository.findByNameIgnoreCase(name).orElseThrow(ApplicationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Application validateLogin(String applicationName, String user, String password) throws ApplicationNotFoundException, UserInvalidPasswordException {
        var application = applicationRepository.findByNameIgnoreCase(applicationName);
        if (application.isEmpty()) throw new ApplicationNotFoundException();

        if (!user.equalsIgnoreCase(application.get().getUser()) || !passwordEncoder.matches(password, application.get().getPassword()))
            throw new UserInvalidPasswordException();

        return application.get();
    }

    @Transactional(rollbackFor = ApplicationAlreadyExistsException.class)
    public Application save(@NotNull Application application) throws ApplicationAlreadyExistsException, NoSuchAlgorithmException {
        try {
            if (application.getId() == null) {
                application.setPassword(passwordEncoder.encode(application.getPassword()));
            }
            application = applicationRepository.save(application);

            if (!application.getUseDefaultKey() && !applicationKeyService.hasKey(application.getId())) {
                applicationKeyService.save(application);
            }

            applicationRepository.flush();
            return application;
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationAlreadyExistsException();
        }
    }

    @Transactional(rollbackFor = ApplicationInUseException.class)
    public void delete(Long id) throws ApplicationNotFoundException, ApplicationInUseException {
        try {
            applicationRepository.delete(this.internalFindOrThrow(id));
            applicationRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationInUseException();
        }
    }

    @Transactional(readOnly = true)
    public Set<String> getAuthorities(Long id) throws ApplicationNotFoundException {
        var application = this.internalFindOrThrow(id);

        var authorities = new TreeSet<String>();
        for (ProfileResourceAction profileResourceAction : profileResourceActionRepository.findByProfileApplication(application)) {
            authorities.add(profileResourceAction.getAuthority());
        }

        return authorities;
    }

    @Transactional
    public void changePassword(@NotNull Application application, String password, String newPassword) throws UserInvalidPasswordException {
        if (!passwordEncoder.matches(password, application.getPassword())) {
            throw new UserInvalidPasswordException();
        }
        application.setPassword(passwordEncoder.encode(newPassword));
        applicationRepository.save(application);
    }

    private Application internalFindOrThrow(Long id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id).orElseThrow(ApplicationNotFoundException::new);
    }
}
