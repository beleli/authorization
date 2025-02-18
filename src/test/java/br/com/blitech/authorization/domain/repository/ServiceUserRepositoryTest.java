package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.ServiceUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static br.com.blitech.authorization.domain.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceUserRepositoryTest {

    @Autowired
    private ServiceUserRepository serviceUserRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Application application;
    private Profile profile;
    private ServiceUser serviceUser;

    @BeforeEach
    void setUp() {
        application = applicationRepository.save(createApplication());
        profile = createProfile();
        profile.setApplication(application);
        profile = profileRepository.save(profile);
        serviceUser = createServiceUser();
        serviceUser.setApplication(application);
        serviceUser.setProfile(profile);
        serviceUser = serviceUserRepository.save(serviceUser);
    }

    @Test
    void testSaveServiceUser() {
        ServiceUser newUser = new ServiceUser(application, profile, "new_user", "newPassword123");
        ServiceUser savedUser = serviceUserRepository.save(newUser);
        assertNotNull(savedUser.getId());
        assertEquals("new_user", savedUser.getName());
        assertNotNull(savedUser.getCreateDate());
    }

    @Test
    void testFindById() {
        Optional<ServiceUser> foundUser = serviceUserRepository.findById(serviceUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(serviceUser.getName(), foundUser.get().getName());
    }

    @Test
    void testFindByApplicationIdAndName() {
        Optional<ServiceUser> foundUser = serviceUserRepository.findByApplicationIdAndName(application.getId(), "user");
        assertTrue(foundUser.isPresent());
        assertEquals(serviceUser.getName(), foundUser.get().getName());
    }

    @Test
    void testFindByIdAndApplicationId() {
        Optional<ServiceUser> foundUser = serviceUserRepository.findByIdAndApplicationId(serviceUser.getId(), application.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(serviceUser.getName(), foundUser.get().getName());
    }

    @Test
    void testFindByApplicationId() {
        Set<ServiceUser> users = serviceUserRepository.findByApplicationId(application.getId());
        assertFalse(users.isEmpty());
        assertTrue(users.contains(serviceUser));
    }

    @Test
    void testDeleteServiceUser() {
        serviceUserRepository.delete(serviceUser);
        Optional<ServiceUser> deletedUser = serviceUserRepository.findById(serviceUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testUpdateServiceUser() {
        serviceUser.setName("updated_user");
        ServiceUser updatedUser = serviceUserRepository.save(serviceUser);
        serviceUserRepository.flush();
        assertEquals("updated_user", updatedUser.getName());
        assertNotNull(updatedUser.getUpdateDate());
    }
}