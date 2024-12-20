package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createProfileResourceAction;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private DomainService domainService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ProfileResourceActionRepository profileResourceActionRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserAuthorities() throws UserInvalidPasswordException, ApplicationNotFoundException {
        Application application = createApplication();
        String username = "testUser";
        String password = "testPassword";
        List<String> adGroups = Collections.singletonList("testGroup");
        ProfileResourceAction action = createProfileResourceAction();

        when(domainService.authenticate(username, password)).thenReturn(true);
        when(domainService.findGroupsByUser(username)).thenReturn(adGroups);
        when(profileResourceActionRepository.findByApplicationAndGroups(application.getId(), adGroups))
                .thenReturn(Collections.singletonList(action));

        Set<String> authorities = userService.getUserAuthorities(application, username, password);
        assertTrue(authorities.contains("RESOURCE.ACTION"));

        when(domainService.authenticate(username, password)).thenReturn(false);
        assertThrows(UserInvalidPasswordException.class, () -> userService.getUserAuthorities(application, username, password));
    }
}
