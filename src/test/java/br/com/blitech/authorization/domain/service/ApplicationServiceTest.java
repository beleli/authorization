package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.repository.ApplicationRepository;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static br.com.blitech.authorization.domain.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationKeyService applicationKeyService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProfileResourceActionRepository profileResourceActionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrThrow() throws ApplicationNotFoundException {
        Application application = createApplication();
        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));

        Application result = applicationService.findOrThrow(application.getId());
        assertEquals(application, result);

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.findOrThrow(application.getId()));
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<Application> page = new PageImpl<>(Collections.emptyList());
        when(applicationRepository.findAll(pageable)).thenReturn(page);

        Page<Application> result = applicationService.findAll(pageable);
        assertEquals(page, result);
    }

    @Test
    void testFindByNameOrThrow() throws ApplicationNotFoundException {
        String name = "testApp";
        Application application = createApplication();
        when(applicationRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(application));

        Application result = applicationService.findByNameOrThrow(name);
        assertEquals(application, result);

        when(applicationRepository.findByNameIgnoreCase(name)).thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.findByNameOrThrow(name));
    }

    @Test
    void testValidateLogin() throws ApplicationNotFoundException, UserInvalidPasswordException {
        String user = "user";
        String password = "testPass";
        Application application = createApplication();
        application.setPassword("encodedPass");
        String applicationName = application.getName();

        when(applicationRepository.findByNameIgnoreCase(applicationName)).thenReturn(Optional.of(application));
        when(passwordEncoder.matches(password, application.getPassword())).thenReturn(true);

        Application result = applicationService.validateLogin(applicationName, user, password);
        assertEquals(application, result);

        when(applicationRepository.findByNameIgnoreCase(applicationName)).thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () -> applicationService.validateLogin(applicationName, user, password));

        when(applicationRepository.findByNameIgnoreCase(applicationName)).thenReturn(Optional.of(application));
        when(passwordEncoder.matches(password, application.getPassword())).thenReturn(false);
        assertThrows(UserInvalidPasswordException.class, () -> applicationService.validateLogin(applicationName, user, password));
    }

    @Test
    void testSave() throws ApplicationAlreadyExistsException, NoSuchAlgorithmException {
        Application application = createApplication();
        application.setPassword("testPass");

        when(passwordEncoder.encode(application.getPassword())).thenReturn("encodedPass");
        when(applicationRepository.save(application)).thenReturn(application);

        Application result = applicationService.save(application);
        assertEquals(application, result);
        assertEquals("encodedPass", application.getPassword());

        doThrow(DataIntegrityViolationException.class).when(applicationRepository).save(application);
        assertThrows(ApplicationAlreadyExistsException.class, () -> applicationService.save(application));
    }

    @Test
    void testDelete() throws ApplicationNotFoundException, ApplicationInUseException {
        Long id = 1L;
        Application application = createApplication();

        when(applicationRepository.findById(id)).thenReturn(Optional.of(application));
        doNothing().when(applicationKeyService).deleteKeys(application);
        doNothing().when(applicationRepository).delete(application);

        applicationService.delete(id);

        doThrow(DataIntegrityViolationException.class).when(applicationRepository).delete(application);
        assertThrows(ApplicationInUseException.class, () -> applicationService.delete(id));
    }

    @Test
    void testGetAuthorities() throws ApplicationNotFoundException {
        Long id = 1L;
        Application application = createApplication();
        ProfileResourceAction action = createProfileResourceAction();

        when(applicationRepository.findById(id)).thenReturn(Optional.of(application));
        when(profileResourceActionRepository.findByProfileApplication(application)).thenReturn(Collections.singletonList(action));

        Set<String> authorities = applicationService.getAuthorities(id);
        assertTrue(authorities.contains("RESOURCE.ACTION"));
    }

    @Test
    void testChangePassword() throws UserInvalidPasswordException {
        Application application = createApplication();
        String newPassword = "newPass";
        application.setPassword("encodedOldPass");

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");
        when(applicationRepository.save(application)).thenReturn(application);

        applicationService.changePassword(application, application.getPassword(), newPassword);
        assertEquals("encodedNewPass", application.getPassword());

        when(passwordEncoder.matches(application.getPassword(), application.getPassword())).thenReturn(false);
        assertThrows(UserInvalidPasswordException.class, () -> applicationService.changePassword(application, application.getPassword(), newPassword));
    }
}