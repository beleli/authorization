package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    private Application application;

    @BeforeEach
    void setUp() {
        application = createApplication();
        application = applicationRepository.save(application);
    }

    @Test
    void testSaveApplication() {
        Application newApplication = new Application();
        newApplication.setName("NewApplication");
        newApplication.setUser("NewUser");
        newApplication.setPassword("NewPassword");
        newApplication.setUseDefaultKey(false);
        Application savedApplication = applicationRepository.save(newApplication);
        assertNotNull(savedApplication.getId());
        assertEquals("NewApplication".toLowerCase(), savedApplication.getName());
        assertNotNull(newApplication.getCreateDate());
    }

    @Test
    void testFindById() {
        Optional<Application> foundApplication = applicationRepository.findById(application.getId());
        assertTrue(foundApplication.isPresent());
        assertEquals(application.getName(), foundApplication.get().getName());
    }

    @Test
    void testDeleteApplication() {
        applicationRepository.delete(application);
        Optional<Application> deletedApplication = applicationRepository.findById(application.getId());
        assertFalse(deletedApplication.isPresent());
    }

    @Test
    void testUpdateApplication() {
        application.setName("UpdatedApplication");
        Application updatedApplication = applicationRepository.save(application);
        applicationRepository.flush();
        assertEquals("UpdatedApplication".toLowerCase(), updatedApplication.getName());
        assertNotNull(updatedApplication.getUpdateDate());
    }
}