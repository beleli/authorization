package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import br.com.blitech.authorization.domain.exception.alreadyexistsexception.ProfileAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.entityinuse.ProfileInUseException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationNotFoundException;
import br.com.blitech.authorization.domain.exception.entitynotfound.ProfileNotFoundException;
import br.com.blitech.authorization.domain.repository.ProfileRepository;
import br.com.blitech.authorization.domain.repository.ProfileResourceActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

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
        Set<Profile> profiles = new HashSet<>(Collections.singletonList(createNewProfile()));
        Application application = createApplication();
        application.setProfiles(profiles);
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);

        Set<Profile> result = profileService.findAll(1L);
        assertEquals(profiles, result);
    }

    @Test
    void testFindOrThrow() throws ApplicationNotFoundException, ProfileNotFoundException {
        Application application = createExistentApplication();
        Profile profile = createNewProfile();
        profile.setApplication(application);
        when(applicationService.findOrThrow(any())).thenReturn(application);
        when(profileRepository.findByIdAndApplicationId(anyLong(), anyLong())).thenReturn(Optional.of(profile));

        Profile result = profileService.findOrThrow(1L, 1L);
        assertEquals(profile, result);

        when(profileRepository.findByIdAndApplicationId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(ProfileNotFoundException.class, () -> profileService.findOrThrow(1L, 1L));
    }

    @Test
    void testSave() throws ProfileAlreadyExistsException, ApplicationNotFoundException {
        Profile profile = createNewProfile();
        when(applicationService.findOrThrow(profile.getApplication().getId())).thenReturn(createApplication());
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile result = profileService.save(profile);
        assertEquals(profile, result);

        doThrow(DataIntegrityViolationException.class).when(profileRepository).save(profile);
        assertThrows(ProfileAlreadyExistsException.class, () -> profileService.save(profile));
    }

    @Test
    void testDelete() throws ApplicationNotFoundException {
        Application application = createExistentApplication();
        Profile profile = createNewProfile();
        profile.setApplication(application);
        when(applicationService.findOrThrow(anyLong())).thenReturn(application);
        when(profileRepository.findByIdAndApplicationId(anyLong(), anyLong())).thenReturn(Optional.of(profile));
        doNothing().when(profileRepository).delete(any());

        assertDoesNotThrow(() -> profileService.delete(1L, 1L));

        doThrow(DataIntegrityViolationException.class).when(profileRepository).delete(any(Profile.class));
        assertThrows(ProfileInUseException.class, () -> profileService.delete(1L, 1L));
    }

    @Test
    void testGetProfileAuthorities() throws ProfileNotFoundException {
        String profileName = "testProfile";
        Profile profile = createNewProfile();
        ProfileResourceAction action = createNewProfileResourceAction();
        when(profileRepository.findByName(profileName)).thenReturn(Optional.of(profile));
        when(profileResourceActionRepository.findByProfile(profile)).thenReturn(Collections.singletonList(action));

        Set<String> authorities = profileService.getProfileAuthorities(profileName);
        assertTrue(authorities.contains("RESOURCE.ACTION"));

        when(profileRepository.findByName(profileName)).thenReturn(Optional.empty());
        assertThrows(ProfileNotFoundException.class, () -> profileService.getProfileAuthorities(profileName));
    }
}