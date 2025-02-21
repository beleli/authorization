package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.*;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ServiceUserAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ServiceUserInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.*;
import br.com.blitech.authorization.domain.repository.ServiceUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static br.com.blitech.authorization.domain.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ServiceUserServiceTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ProfileService profileService;

    @Mock
    private ServiceUserRepository serviceUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ServiceUserService serviceUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() throws ApplicationNotFoundException {
        Set<ServiceUser> users = Collections.singleton(createServiceUser());
        Application application = createApplication();
        application.setServiceUsers(users);
        when(applicationService.findOrThrow(any())).thenReturn(application);
        when(serviceUserRepository.findByApplicationId(any())).thenReturn(users);

        Set<ServiceUser> result = serviceUserService.findAll(1L);
        assertEquals(users, result);
    }

    @Test
    void testFindOrThrow() throws ApplicationNotFoundException, ServiceUserNotFoundException {
        Application application = createExistentApplication();
        ServiceUser serviceUser = createServiceUser();
        serviceUser.setApplication(application);
        when(applicationService.findOrThrow(any())).thenReturn(application);
        when(serviceUserRepository.findByApplicationIdAndId(anyLong(), anyLong())).thenReturn(Optional.of(serviceUser));

        ServiceUser result = serviceUserService.findOrThrow(1L, 1L);
        assertEquals(serviceUser, result);

        when(serviceUserRepository.findByApplicationIdAndId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(ServiceUserNotFoundException.class, () -> serviceUserService.findOrThrow(1L, 1L));
    }

    @Test
    void testSave() throws ServiceUserAlreadyExistsException, ServiceUserNotFoundException, ApplicationNotFoundException, ProfileNotFoundException {
        ServiceUser serviceUser = createServiceUser();
        when(applicationService.findOrThrow(serviceUser.getApplication().getId())).thenReturn(createApplication());
        when(serviceUserRepository.save(serviceUser)).thenReturn(serviceUser);

        ServiceUser result = serviceUserService.save(serviceUser);
        assertEquals(serviceUser, result);

        doThrow(DataIntegrityViolationException.class).when(serviceUserRepository).save(serviceUser);
        assertThrows(ServiceUserAlreadyExistsException.class, () -> serviceUserService.save(serviceUser));
    }

    @Test
    void testSaveThrowsServiceUserNotFoundException() throws ApplicationNotFoundException {
        ServiceUser serviceUser = createServiceUser();
        when(applicationService.findOrThrow(serviceUser.getApplication().getId())).thenReturn(createApplication());

        JpaObjectRetrievalFailureException jpaException = mock(JpaObjectRetrievalFailureException.class);
        when(jpaException.getMessage()).thenReturn(ServiceUser.class.getCanonicalName());
        doThrow(jpaException).when(serviceUserRepository).save(serviceUser);

        assertThrows(ServiceUserNotFoundException.class, () -> serviceUserService.save(serviceUser));
    }

    @Test
    void testDelete() throws ApplicationNotFoundException, ServiceUserNotFoundException, ServiceUserInUseException {
        Application application = createExistentApplication();
        ServiceUser serviceUser = createServiceUser();
        serviceUser.setApplication(application);
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);
        when(serviceUserRepository.findByApplicationIdAndId(anyLong(), anyLong())).thenReturn(Optional.of(serviceUser));
        doNothing().when(serviceUserRepository).delete(any());

        assertDoesNotThrow(() -> serviceUserService.delete(1L, 1L));

        doThrow(DataIntegrityViolationException.class).when(serviceUserRepository).delete(any(ServiceUser.class));
        assertThrows(ServiceUserInUseException.class, () -> serviceUserService.delete(1L, 1L));
    }

    @Test
    void testValidateLogin() throws ApplicationNotFoundException, UserInvalidPasswordException {
        Application application = createApplication();
        ServiceUser serviceUser = createServiceUser();
        when(applicationService.findByNameOrThrow(anyString())).thenReturn(application);
        when(serviceUserRepository.findByApplicationIdAndName(any(), anyString())).thenReturn(Optional.of(serviceUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        ServiceUser result = serviceUserService.validateLogin("appName", "user", "password");
        assertEquals(serviceUser, result);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(UserInvalidPasswordException.class, () -> serviceUserService.validateLogin("appName", "user", "password"));
    }

    @Test
    void testGetAuthorities() throws ApplicationNotFoundException, ServiceUserNotFoundException, ProfileNotFoundException {
        Application application = createApplication();
        ServiceUser serviceUser = createServiceUser();
        serviceUser.setProfile(null);
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);
        when(serviceUserRepository.findByApplicationIdAndId(any(), any())).thenReturn(Optional.of(serviceUser));
        when(applicationService.getAuthorities(any())).thenReturn(Collections.singleton("ROLE_USER"));

        Set<String> authorities = serviceUserService.getAuthorities(1L, 1L);
        assertTrue(authorities.contains("ROLE_USER"));

        when(serviceUserRepository.findByApplicationIdAndId(any(), any())).thenReturn(Optional.empty());
        assertThrows(ServiceUserNotFoundException.class, () -> serviceUserService.getAuthorities(1L, 1L));
    }

    @Test
    void testChangePassword() throws UserInvalidPasswordException {
        ServiceUser serviceUser = createServiceUser();
        String newPassword = "newPass";
        serviceUser.setPassword("encodedOldPass");

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");
        when(serviceUserRepository.save(any())).thenReturn(serviceUser);

        serviceUserService.changePassword(serviceUser, serviceUser.getPassword(), newPassword);
        assertEquals("encodedNewPass", serviceUser.getPassword());

        when(passwordEncoder.matches(serviceUser.getPassword(), serviceUser.getPassword())).thenReturn(false);
        assertThrows(UserInvalidPasswordException.class, () -> serviceUserService.changePassword(serviceUser, serviceUser.getPassword(), newPassword));
    }
}