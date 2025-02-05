package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.api.v1.assembler.ProfileModelAssembler;
import br.com.blitech.authorization.api.v1.model.ProfileModel;
import br.com.blitech.authorization.api.v1.model.input.ProfileInputModel;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ProfileAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import br.com.blitech.authorization.domain.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static br.com.blitech.authorization.api.TestUtlis.createProfileInputModel;
import static br.com.blitech.authorization.api.TestUtlis.createProfileModel;
import static br.com.blitech.authorization.domain.TestUtils.createProfile;
import static org.hibernate.internal.util.collections.CollectionHelper.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private ProfileModelAssembler profileModelAssembler;

    @InjectMocks
    private ProfileController profileController;

    private Profile profile = createProfile();
    private ProfileModel profileModel = createProfileModel();
    private ProfileInputModel profileInputModel = createProfileInputModel();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(authorities = "PROFILES.READ")
    void testFindAll() throws ApplicationNotFoundException {
        when(profileService.findAll(anyLong())).thenReturn(setOf(profile));
        when(profileModelAssembler.toModel(any())).thenReturn(profileModel);

        Set<ProfileModel> result = profileController.findAll(1L);

        assertNotNull(result);
        verify(profileService, times(1)).findAll(1L);
    }

    @Test
    @WithMockUser(authorities = "PROFILES.READ")
    void testFind() throws ProfileNotFoundException, ApplicationNotFoundException {
        when(profileService.findOrThrow(anyLong(), anyLong())).thenReturn(profile);
        when(profileModelAssembler.toModel(any())).thenReturn(profileModel);

        ProfileModel result = profileController.findById(1L, 1L);

        assertNotNull(result);
        assertEquals(profile.getName(), result.getName());
        verify(profileService, times(1)).findOrThrow(1L, 1L);
    }

    @Test
    @WithMockUser(authorities = "PROFILES.WRITE")
    void testInsert() throws BusinessException {
        MockedStatic mockedStatic = mockStatic(ResourceUriHelper.class);

        when(profileService.save(any())).thenReturn(profile);
        when(profileModelAssembler.toEntity(anyLong(), any())).thenReturn(profile);
        when(profileModelAssembler.toModel(any())).thenReturn(profileModel);

        ProfileModel result = profileController.insert(1L, profileInputModel);

        assertNotNull(result);
        assertEquals(profile.getName(), result.getName());
        verify(profileService, times(1)).save(any());

        mockedStatic.close();
    }

    @Test
    @WithMockUser(authorities = "PROFILES.WRITE")
    void testUpdate() throws BusinessException {
        when(profileService.findOrThrow(anyLong(), anyLong())).thenReturn(profile);
        when(profileService.save(any())).thenReturn(profile);
        when(profileModelAssembler.applyModel(anyLong(), any(), any())).thenReturn(profile);
        when(profileModelAssembler.toModel(any())).thenReturn(profileModel);

        ProfileModel result = profileController.update(1L, 1L, profileInputModel);

        assertNotNull(result);
        assertEquals(profile.getName(), result.getName());
        verify(profileService, times(1)).findOrThrow(1L, 1L);
        verify(profileService, times(1)).save(any());
    }

    @Test
    @WithMockUser(authorities = "PROFILES.WRITE")
    void testDelete() throws BusinessException {
        doNothing().when(profileService).delete(anyLong(), anyLong());

        profileController.delete(1L, 1L);

        verify(profileService, times(1)).delete(1L, 1L);
    }
}