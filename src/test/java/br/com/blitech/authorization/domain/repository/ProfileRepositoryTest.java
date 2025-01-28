package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createProfile;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Application application;
    private Profile profile;

    @BeforeEach
    void setUp() {
        application = applicationRepository.save(createApplication());
        profile = createProfile();
        profile.setApplication(application);
        profile = profileRepository.save(profile);
    }

    @Test
    void testSaveProfile() {
        Profile newProfile = new Profile();
        newProfile.setName("NewProfile");
        newProfile.setGroup("NewGroup");
        newProfile.setApplication(application);
        Profile savedProfile = profileRepository.save(newProfile);
        assertNotNull(savedProfile.getId());
        assertEquals("NewProfile".toLowerCase(), savedProfile.getName());
        assertNotNull(newProfile.getCreateDate());
    }

    @Test
    void testFindById() {
        Optional<Profile> foundProfile = profileRepository.findById(profile.getId());
        assertTrue(foundProfile.isPresent());
        assertEquals(profile.getName(), foundProfile.get().getName());
    }

    @Test
    void testDeleteProfile() {
        profileRepository.delete(profile);
        Optional<Profile> deletedProfile = profileRepository.findById(profile.getId());
        assertFalse(deletedProfile.isPresent());
    }

    @Test
    void testUpdateProfile() {
        profile.setName("UpdatedProfile");
        Profile updatedProfile = profileRepository.save(profile);
        profileRepository.flush();
        assertEquals("UpdatedProfile".toLowerCase(), updatedProfile.getName());
        assertNotNull(updatedProfile.getUpdateDate());
    }
}