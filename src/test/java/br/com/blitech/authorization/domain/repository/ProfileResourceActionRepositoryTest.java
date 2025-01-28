package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileResourceActionRepositoryTest {

    @Autowired
    private ProfileResourceActionRepository profileResourceActionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ActionRepository actionRepository;

    private Application application;
    private Profile profile;
    private Resource resource;
    private Action action;
    private ProfileResourceAction profileResourceAction;

    @BeforeEach
    void setUp() {
        application = applicationRepository.save(createApplication());
        profile = createProfile();
        profile.setApplication(application);
        profile = profileRepository.save(profile);
        resource = resourceRepository.save(createResource());
        action = actionRepository.save(createAction());
        profileResourceAction = new ProfileResourceAction(profile, resource, action);
        profileResourceAction = profileResourceActionRepository.save(profileResourceAction);
    }

    @Test
    void testSaveProfileResourceAction() {
        ProfileResourceAction newProfileResourceAction = new ProfileResourceAction(profile, resource, action);
        ProfileResourceAction savedProfileResourceAction = profileResourceActionRepository.save(newProfileResourceAction);
        assertNotNull(savedProfileResourceAction.getId());
        assertEquals(profile.getId(), savedProfileResourceAction.getProfile().getId());
        assertEquals(resource.getId(), savedProfileResourceAction.getResource().getId());
        assertEquals(action.getId(), savedProfileResourceAction.getAction().getId());
    }

    @Test
    void testFindById() {
        Optional<ProfileResourceAction> foundProfileResourceAction = profileResourceActionRepository.findById(profileResourceAction.getId());
        assertTrue(foundProfileResourceAction.isPresent());
        assertEquals(profileResourceAction.getProfile().getId(), foundProfileResourceAction.get().getProfile().getId());
    }

    @Test
    void testDeleteProfileResourceAction() {
        profileResourceActionRepository.delete(profileResourceAction);
        Optional<ProfileResourceAction> deletedProfileResourceAction = profileResourceActionRepository.findById(profileResourceAction.getId());
        assertFalse(deletedProfileResourceAction.isPresent());
    }

    @Test
    void testUpdateProfileResourceAction() {
        profileResourceAction.getProfile().setName("UpdatedProfile");
        ProfileResourceAction updatedProfileResourceAction = profileResourceActionRepository.save(profileResourceAction);
        profileResourceActionRepository.flush();
        assertEquals("UpdatedProfile".toLowerCase(), updatedProfileResourceAction.getProfile().getName());
    }
}