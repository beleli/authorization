package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.api.v1.model.LoginModel;
import br.com.blitech.authorization.api.v1.model.input.LoginApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.LoginUserInputModel;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationService;
import br.com.blitech.authorization.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.util.Set;

import static br.com.blitech.authorization.api.TestUtlis.*;
import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private JwtKeyProvider jwtKeyProvider;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        MockitoAnnotations.openMocks(this);
        when(jwtKeyProvider.getKey()).thenReturn(generateKeyPair().getPrivate());
    }

    @Test
    void testLoginApplication() throws UserNotAuthorizedException, ApplicationNotFoundException, UserInvalidPasswordException {
        LoginApplicationInputModel loginApplicationInputModel = createLoginApplicationInputModel();
        var application = createApplication();
        when(applicationService.validateLogin(any(), any())).thenReturn(application);
        when(applicationService.getAuthorities(any())).thenReturn(Set.of("RESOURCE.ACTION"));

        LoginModel result = loginController.loginApplication(loginApplicationInputModel);

        assertNotNull(result);
        assertNotNull(result.getToken());
        verify(applicationService, times(1)).validateLogin(any(), any());
        verify(applicationService, times(1)).getAuthorities(any());
    }

    @Test
    void testLoginUser() throws BusinessException {
        LoginUserInputModel loginUserInputModel = createLoginUserInputModel();
        var application = createApplication();
        when(applicationService.findByNameOrThrow(any())).thenReturn(application);
        when(userService.getAuthorities(any(), any(), any())).thenReturn(Set.of("RESOURCE.ACTION"));

        LoginModel result = loginController.loginUser(loginUserInputModel);

        assertNotNull(result);
        assertNotNull(result.getToken());
        verify(applicationService, times(1)).findByNameOrThrow(any());
        verify(userService, times(1)).getAuthorities(any(), any(), any());
    }
}
