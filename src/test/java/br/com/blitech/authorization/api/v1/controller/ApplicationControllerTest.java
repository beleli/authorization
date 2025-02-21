package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ApplicationModelAssembler;
import br.com.blitech.authorization.api.v1.model.ApplicationModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationInputModel;
import br.com.blitech.authorization.api.v1.model.input.ApplicationPasswordInputModel;
import br.com.blitech.authorization.api.v1.model.input.ChangePasswordInputModel;
import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ApplicationKey;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ApplicationAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.business.UserInvalidPasswordException;
import br.com.blitech.authorization.domain.exception.entityinuse.ApplicationInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.service.ApplicationKeyService;
import br.com.blitech.authorization.domain.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import static br.com.blitech.authorization.api.TestUtlis.*;
import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createApplicationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationKeyService applicationKeyService;

    @Mock
    private ApplicationModelAssembler applicationModelAssembler;

    @InjectMocks
    private ApplicationController applicationController;

    private Application application = createApplication();
    private ApplicationModel applicationModel = createApplicationModel();
    private ApplicationInputModel applicationInputModel = createApplicationInputModel();
    private ApplicationPasswordInputModel applicationPasswordInputModel = createApplicationPasswordInputModel();
    private ChangePasswordInputModel changePasswordInputModel = createChangePasswordInputModel();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> page = new PageImpl<>(Collections.singletonList(application));
        when(applicationService.findAll(pageable)).thenReturn(page);
        when(applicationModelAssembler.toModel(any())).thenReturn(applicationModel);

        Page<ApplicationModel> result = applicationController.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(applicationService, times(1)).findAll(pageable);
    }

    @Test
    void testFind() throws ApplicationNotFoundException {
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);
        when(applicationModelAssembler.toModel(any())).thenReturn(applicationModel);

        ApplicationModel result = applicationController.findById(1L);

        assertNotNull(result);
        assertEquals(application.getName(), result.getName());
        verify(applicationService, times(1)).findOrThrow(1L);
    }

    @Test
    void testInsert() throws ApplicationAlreadyExistsException, NoSuchAlgorithmException {
        MockedStatic mockedStatic = mockStatic(ResourceUriHelper.class);

        when(applicationService.save(any())).thenReturn(application);
        when(applicationModelAssembler.toEntity(any())).thenReturn(application);
        when(applicationModelAssembler.toModel(any())).thenReturn(applicationModel);

        ApplicationModel result = applicationController.insert(applicationPasswordInputModel);

        assertNotNull(result);
        assertEquals(application.getName(), result.getName());
        verify(applicationService, times(1)).save(any());

        mockedStatic.close();
    }

    @Test
    void testUpdate() throws ApplicationAlreadyExistsException, ApplicationNotFoundException, NoSuchAlgorithmException {
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);
        when(applicationService.save(any())).thenReturn(application);
        when(applicationModelAssembler.applyModel(any(), any())).thenReturn(application);
        when(applicationModelAssembler.toModel(any())).thenReturn(applicationModel);

        ApplicationModel result = applicationController.update(1L, applicationInputModel);

        assertNotNull(result);
        assertEquals(application.getName(), result.getName());
        verify(applicationService, times(1)).findOrThrow(1L);
        verify(applicationService, times(1)).save(any());
    }

    @Test
    void testDelete() throws ApplicationNotFoundException, ApplicationInUseException {
        doNothing().when(applicationService).delete(anyLong());

        applicationController.delete(1L);

        verify(applicationService, times(1)).delete(1L);
    }

    @Test
    void testChangePassword() throws ApplicationNotFoundException, UserInvalidPasswordException {
        when(applicationService.findOrThrow(any())).thenReturn(application);
        doNothing().when(applicationService).changePassword(any(), any(), any());

        applicationController.changePassword(1L, changePasswordInputModel);

        verify(applicationService, times(1)).changePassword(application, changePasswordInputModel.getPassword(), changePasswordInputModel.getNewPassword());
    }

    @Test
    void testInesrtKey() throws ApplicationNotFoundException, NoSuchAlgorithmException {
        when(applicationService.findOrThrow(any())).thenReturn(application);
        when(applicationKeyService.save(any())).thenReturn(createApplicationKey());

        applicationController.insertKey(1L);

        verify(applicationKeyService, times(1)).save(application);
    }
}
