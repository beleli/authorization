package br.com.blitech.authorization.domain.service;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.ApplicationKey;
import br.com.blitech.authorization.domain.exception.entitynotfound.ApplicationKeyNotFoundException;
import br.com.blitech.authorization.domain.repository.ApplicationKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createApplicationKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationKeyServiceTest {

    @Mock
    private ApplicationKeyRepository applicationKeyRepository;

    @InjectMocks
    private ApplicationKeyService applicationKeyService;

    private Application application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        application = createApplication();
    }

    @Test
    void testSave() throws NoSuchAlgorithmException {
        ApplicationKey applicationKey = createApplicationKey();
        when(applicationKeyRepository.countByApplicationId(any())).thenReturn(0L);
        when(applicationKeyRepository.save(any())).thenReturn(applicationKey);

        var result = applicationKeyService.save(application);
        assertEquals(applicationKey, result);
    }

    @Test
    void testHasKey() {
        when(applicationKeyRepository.countByApplicationId(any())).thenReturn(0L);

        Boolean result = applicationKeyService.hasKey(application.getId());
        assertEquals(false, result);
    }

    @Test
    void testGetLastPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, ApplicationKeyNotFoundException {
        ApplicationKey applicationKey = createApplicationKey();
        when(applicationKeyRepository.findByApplicationIdOrderByIdDesc(any())).thenReturn(List.of(applicationKey));

        Key privateKey = applicationKeyService.getLastPrivateKey(application);
        assertNotNull(privateKey);
    }

    @Test
    void testGetLastPrivateKeyThrowsNotFound() {
        when(applicationKeyRepository.findByApplicationIdOrderByIdDesc(any())).thenReturn(List.of());

        assertThrows(ApplicationKeyNotFoundException.class, () -> applicationKeyService.getLastPrivateKey(application));
    }

    @Test
    void testGetLastPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, ApplicationKeyNotFoundException {
        ApplicationKey applicationKey = createApplicationKey();
        when(applicationKeyRepository.findByApplicationIdOrderByIdDesc(any())).thenReturn(List.of(applicationKey));

        Pair<Long, Key> publicKeyPair = applicationKeyService.getLastPublicKey(application);
        assertNotNull(publicKeyPair);
        assertEquals(1L, publicKeyPair.getFirst());
    }

    @Test
    void testGetLastPublicKeyNotFound() {
        when(applicationKeyRepository.findByApplicationIdOrderByIdDesc(any(Long.class))).thenReturn(List.of());

        assertThrows(ApplicationKeyNotFoundException.class, () -> applicationKeyService.getLastPublicKey(application));
    }

    @Test
    void testDeleteKeys() throws NoSuchAlgorithmException {
        ApplicationKey applicationKey = createApplicationKey();
        when(applicationKeyRepository.findByApplicationIdOrderByIdDesc(any())).thenReturn(List.of(applicationKey));

        applicationKeyService.deleteKeys(application);
        verify(applicationKeyRepository).delete(applicationKey);
    }
}