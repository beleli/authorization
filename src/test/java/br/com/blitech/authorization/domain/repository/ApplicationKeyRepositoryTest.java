package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ApplicationKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createApplicationKey;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApplicationKeyRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationKeyRepository applicationKeyRepository;

    private Application application;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        application = applicationRepository.save(createApplication());
        ApplicationKey applicationKey = createApplicationKey();
        applicationKey.setApplication(application);
        applicationKeyRepository.save(applicationKey);
    }

    @Test
    void testCountByApplicationId() {
        Long count = applicationKeyRepository.countByApplicationId(application.getId());
        assertEquals(1, count);
    }

    @Test
    void testFindByApplicationIdOrderByIdDesc() {
        List<ApplicationKey> keys = applicationKeyRepository.findByApplicationIdOrderByIdDesc(application.getId());
        assertFalse(keys.isEmpty());
    }
}