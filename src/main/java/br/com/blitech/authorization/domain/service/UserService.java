package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class UserService {
    private final DomainService domainService;
    private final ProfileResourceActionRepository profileResourceActionRepository;

    @Autowired
    public UserService(DomainService domainService, ProfileResourceActionRepository profileResourceActionRepository) {
        this.domainService = domainService;
        this.profileResourceActionRepository = profileResourceActionRepository;
    }

    @Transactional(readOnly = true)
    public Set<String> getAuthorities(Application application, String username, String password) throws UserInvalidPasswordException, ApplicationNotFoundException {
        if (!domainService.authenticate(username, password)) {
            throw new UserInvalidPasswordException();
        }
        List<String> adGroups = domainService.findGroupsByUser(username);
        Set<String> authorities = new TreeSet<>();
        for (ProfileResourceAction profileResourceAction : profileResourceActionRepository.findByApplicationAndGroups(application.getId(), adGroups)) {
            authorities.add(profileResourceAction.getAuthority());
        }

        return authorities;
    }
}
