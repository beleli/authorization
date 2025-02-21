package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ServiceUserModelAssembler;
import br.com.blitech.authorization.api.v1.model.ServiceUserModel;
import br.com.blitech.authorization.api.v1.model.input.ChangePasswordInputModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserInputModel;
import br.com.blitech.authorization.api.v1.model.input.ServiceUserPasswordInputModel;
import br.com.blitech.authorization.domain.entity.ServiceUser;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ServiceUserNotFoundException;
import br.com.blitech.authorization.domain.service.ServiceUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static br.com.blitech.authorization.api.TestUtlis.*;
import static br.com.blitech.authorization.domain.TestUtils.createServiceUser;
import static org.hibernate.internal.util.collections.CollectionHelper.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceUserControllerTest {

    @Mock
    private ServiceUserService serviceUserService;

    @Mock
    private ServiceUserModelAssembler serviceUserModelAssembler;

    @InjectMocks
    private ServiceUserController serviceUserController;

    private ServiceUser serviceUser = createServiceUser();
    private ServiceUserModel serviceUserModel = createServiceUserModel();
    private ServiceUserInputModel serviceUserInputModel = createServiceUserInputModel();
    private ServiceUserPasswordInputModel servicePasswordUserInputModel = createServiceUserPasswordInputModel();
    private ChangePasswordInputModel changePasswordInputModel = createChangePasswordInputModel();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(authorities = "USERS.READ")
    void testFindAll() throws ApplicationNotFoundException {
        when(serviceUserService.findAll(anyLong())).thenReturn(setOf(serviceUser));
        when(serviceUserModelAssembler.toModel(any())).thenReturn(serviceUserModel);

        Set<ServiceUserModel> result = serviceUserController.findAll(1L);

        assertNotNull(result);
        verify(serviceUserService, times(1)).findAll(1L);
    }

    @Test
    @WithMockUser(authorities = "USERS.READ")
    void testFindById() throws ServiceUserNotFoundException, ApplicationNotFoundException {
        when(serviceUserService.findOrThrow(anyLong(), anyLong())).thenReturn(serviceUser);
        when(serviceUserModelAssembler.toModel(any())).thenReturn(serviceUserModel);

        ServiceUserModel result = serviceUserController.findById(1L, 1L);

        assertNotNull(result);
        assertEquals(serviceUser.getName(), result.getName());
        verify(serviceUserService, times(1)).findOrThrow(1L, 1L);
    }

    @Test
    @WithMockUser(authorities = "USERS.WRITE")
    void testInsert() throws BusinessException {
        MockedStatic<ResourceUriHelper> mockedStatic = mockStatic(ResourceUriHelper.class);

        when(serviceUserService.save(any())).thenReturn(serviceUser);
        when(serviceUserModelAssembler.toEntity(anyLong(), any())).thenReturn(serviceUser);
        when(serviceUserModelAssembler.toModel(any())).thenReturn(serviceUserModel);

        ServiceUserModel result = serviceUserController.insert(1L, servicePasswordUserInputModel);

        assertNotNull(result);
        assertEquals(serviceUser.getName(), result.getName());
        verify(serviceUserService, times(1)).save(any());

        mockedStatic.close();
    }

    @Test
    @WithMockUser(authorities = "USERS.WRITE")
    void testUpdate() throws BusinessException {
        when(serviceUserService.findOrThrow(anyLong(), anyLong())).thenReturn(serviceUser);
        when(serviceUserService.save(any())).thenReturn(serviceUser);
        when(serviceUserModelAssembler.applyModel(any(), any())).thenReturn(serviceUser);
        when(serviceUserModelAssembler.toModel(any())).thenReturn(serviceUserModel);

        ServiceUserModel result = serviceUserController.update(1L, 1L, serviceUserInputModel);

        assertNotNull(result);
        assertEquals(serviceUser.getName(), result.getName());
        verify(serviceUserService, times(1)).findOrThrow(1L, 1L);
        verify(serviceUserService, times(1)).save(any());
    }

    @Test
    @WithMockUser(authorities = "USERS.WRITE")
    void testDelete() throws BusinessException {
        doNothing().when(serviceUserService).delete(anyLong(), anyLong());

        serviceUserController.delete(1L, 1L);

        verify(serviceUserService, times(1)).delete(1L, 1L);
    }

    @Test
    void testChangePassword() throws ApplicationNotFoundException, ServiceUserNotFoundException, UserInvalidPasswordException {
        when(serviceUserService.findOrThrow(anyLong(), anyLong())).thenReturn(serviceUser);
        doNothing().when(serviceUserService).changePassword(any(), any(), any());

        serviceUserController.changePassword(1L, 1L, changePasswordInputModel);

        verify(serviceUserService, times(1)).changePassword(serviceUser, changePasswordInputModel.getPassword(), changePasswordInputModel.getNewPassword());
    }
}