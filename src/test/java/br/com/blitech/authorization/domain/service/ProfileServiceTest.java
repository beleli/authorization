package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.*;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ProfileAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ProfileInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ActionNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ResourceNotFoundException;
import br.com.blitech.authorization.domain.repository.ProfileRepository;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static br.com.blitech.authorization.domain.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileResourceActionRepository profileResourceActionRepository;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() throws ApplicationNotFoundException {
        Set<Profile> profiles = new HashSet<>(Collections.singletonList(createProfile()));
        Application application = createApplication();
        application.setProfiles(profiles);
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);

        Set<Profile> result = profileService.findAll(1L);
        assertEquals(profiles, result);
    }

    @Test
    void testFindOrThrow() throws ApplicationNotFoundException, ProfileNotFoundException {
        Application application = createExistentApplication();
        Profile profile = createProfile();
        profile.setApplication(application);
        when(applicationService.findOrThrow(any())).thenReturn(application);
        when(profileRepository.findByApplicationIdAndId(anyLong(), anyLong())).thenReturn(Optional.of(profile));

        Profile result = profileService.findOrThrow(1L, 1L);
        assertEquals(profile, result);

        when(profileRepository.findByApplicationIdAndId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(ProfileNotFoundException.class, () -> profileService.findOrThrow(1L, 1L));
    }

    @Test
    void testSave() throws ProfileAlreadyExistsException, ApplicationNotFoundException, ActionNotFoundException, ResourceNotFoundException, ProfileNotFoundException {
        Profile profile = createProfile();
        when(applicationService.findOrThrow(profile.getApplication().getId())).thenReturn(createApplication());
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = profileService.save(profile);
        assertEquals(profile, result);

        doThrow(DataIntegrityViolationException.class).when(profileRepository).save(profile);
        assertThrows(ProfileAlreadyExistsException.class, () -> profileService.save(profile));
    }

    @Test
    void testSaveThrowsProfileNotFoundException() throws ApplicationNotFoundException {
        Profile profile = createProfile();
        when(applicationService.findOrThrow(profile.getApplication().getId())).thenReturn(createApplication());

        JpaObjectRetrievalFailureException jpaException = mock(JpaObjectRetrievalFailureException.class);
        when(jpaException.getMessage()).thenReturn(Profile.class.getCanonicalName());
        doThrow(jpaException).when(profileRepository).save(profile);

        assertThrows(ProfileNotFoundException.class, () -> profileService.save(profile));
    }

    @Test
    void testSaveThrowsResourceNotFoundException() throws ApplicationNotFoundException {
        Profile profile = createProfile();
        when(applicationService.findOrThrow(profile.getApplication().getId())).thenReturn(createApplication());

        JpaObjectRetrievalFailureException jpaException = mock(JpaObjectRetrievalFailureException.class);
        when(jpaException.getMessage()).thenReturn(Resource.class.getCanonicalName());
        doThrow(jpaException).when(profileRepository).save(profile);

        assertThrows(ResourceNotFoundException.class, () -> profileService.save(profile));
    }

    @Test
    void testSaveThrowsActionNotFoundException() throws ApplicationNotFoundException {
        Profile profile = createProfile();
        when(applicationService.findOrThrow(profile.getApplication().getId())).thenReturn(createApplication());

        JpaObjectRetrievalFailureException jpaException = mock(JpaObjectRetrievalFailureException.class);
        when(jpaException.getMessage()).thenReturn(Action.class.getCanonicalName());
        doThrow(jpaException).when(profileRepository).save(profile);

        assertThrows(ActionNotFoundException.class, () -> profileService.save(profile));
    }

    @Test
    void testDelete() throws ApplicationNotFoundException {
        Application application = createExistentApplication();
        Profile profile = createProfile();
        profile.setApplication(application);
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);
        when(profileRepository.findByApplicationIdAndId(anyLong(), anyLong())).thenReturn(Optional.of(profile));
        doNothing().when(profileRepository).delete(any());

        assertDoesNotThrow(() -> profileService.delete(1L, 1L));

        doThrow(DataIntegrityViolationException.class).when(profileRepository).delete(any(Profile.class));
        assertThrows(ProfileInUseException.class, () -> profileService.delete(1L, 1L));
    }

    @Test
    void testGetAuthorities() throws ProfileNotFoundException {
        Profile profile = createProfile();
        ProfileResourceAction action = createProfileResourceAction();
        when(profileRepository.findById(any())).thenReturn(Optional.of(profile));
        when(profileResourceActionRepository.findByProfile(profile)).thenReturn(Collections.singletonList(action));

        Set<String> authorities = profileService.getAuthorities(1L);
        assertTrue(authorities.contains("RESOURCE.ACTION"));

        when(profileRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ProfileNotFoundException.class, () -> profileService.getAuthorities(1L));
    }
}