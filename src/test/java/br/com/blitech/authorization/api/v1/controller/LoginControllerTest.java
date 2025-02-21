package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationKeyNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationKeyService;
import br.com.blitech.authorization.domain.service.ApplicationService;
import br.com.blitech.authorization.domain.service.ServiceUserService;
import br.com.blitech.authorization.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

import static br.com.blitech.authorization.api.TestUtlis.*;
import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createServiceUser;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private JwtKeyProvider jwtKeyProvider;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ServiceUserService serviceUserService;

    @Mock
    private ApplicationKeyService applicationKeyService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, ApplicationKeyNotFoundException, InvalidKeySpecException {
        MockitoAnnotations.openMocks(this);
        var privateKey = generateKeyPair().getPrivate();
        when(jwtKeyProvider.getPrivateKey()).thenReturn(privateKey);
        when(applicationKeyService.getLastPrivateKey(any())).thenReturn(privateKey);
    }

    @Test
    void testLoginApplication() throws UserNotAuthorizedException, ApplicationNotFoundException, UserInvalidPasswordException {
        LoginInputModel loginApplicationInputModel = createLoginInputModel();
        var application = createApplication();
        application.setUseDefaultKey(true);
        when(applicationService.validateLogin(any(),any(), any())).thenReturn(application);
        when(applicationService.getAuthorities(any())).thenReturn(Set.of("RESOURCE.ACTION"));

        LoginModel result = loginController.loginApplication(loginApplicationInputModel);

        assertNotNull(result);
        assertNotNull(result.getToken());
        verify(applicationService, times(1)).validateLogin(any(),any(), any());
        verify(applicationService, times(1)).getAuthorities(any());
    }

    @Test
    void testLoginUser() throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        LoginInputModel loginInputModel = createLoginInputModel();
        var application = createApplication();
        when(applicationService.findByNameOrThrow(any())).thenReturn(application);
        when(userService.getAuthorities(any(), any(), any())).thenReturn(Set.of("RESOURCE.ACTION"));

        LoginModel result = loginController.loginUser(loginInputModel);

        assertNotNull(result);
        assertNotNull(result.getToken());
        verify(applicationService, times(1)).findByNameOrThrow(any());
        verify(userService, times(1)).getAuthorities(any(), any(), any());
    }

    @Test
    void testLoginServiceUser() throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        LoginInputModel loginInputModel = createLoginInputModel();
        when(serviceUserService.validateLogin(any(), any(), any())).thenReturn(createServiceUser());
        when(serviceUserService.getAuthorities(any(), any())).thenReturn(Set.of("RESOURCE.ACTION"));

        LoginModel result = loginController.loginServiceUser(loginInputModel);

        assertNotNull(result);
        assertNotNull(result.getToken());
        verify(serviceUserService, times(1)).validateLogin(any(), any(), any());
        verify(serviceUserService, times(1)).getAuthorities(any(), any());
    }
}
