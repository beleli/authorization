package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.security.JwtKeyProvider;
import br.com.blitech.authorization.domain.service.ApplicationKeyService;
import br.com.blitech.authorization.domain.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static br.com.blitech.authorization.domain.TestUtils.createApplication;
import static br.com.blitech.authorization.domain.TestUtils.createApplicationKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtKeyControllerTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationKeyService applicationKeyService;

    @Mock
    private JwtKeyProvider jwtKeyProvider;

    @InjectMocks
    private JwtKeyController jwtKeyController;

    private RSAPublicKey mockPublicKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPublicKey = mock(RSAPublicKey.class);

        when(jwtKeyProvider.getPublicKey()).thenReturn(mockPublicKey);
        when(jwtKeyProvider.getKeyId()).thenReturn(0L);

        when(mockPublicKey.getModulus()).thenReturn(new BigInteger(1, new byte[]{1, 2, 3, 4}));
        when(mockPublicKey.getPublicExponent()).thenReturn(BigInteger.valueOf(65537));
    }

    @Test
    void testGetJwks() {
        var key = jwtKeyController.getJwks();

        assertNotNull(key);
        assertEquals("RSA", key.getKty());
        assertEquals("sig", key.getUse());
        assertEquals("0", key.getKid());
        assertEquals("RS256", key.getAlg());
        assertEquals(
                Base64.getUrlEncoder().withoutPadding().encodeToString(new BigInteger(1, new byte[]{1, 2, 3, 4}).toByteArray()),
                key.getN());
        assertEquals(
                Base64.getUrlEncoder().withoutPadding().encodeToString(BigInteger.valueOf(65537).toByteArray()),
                key.getE());
    }

    @Test
    void testGetApplicationJwks() throws Exception {
        when(applicationService.findOrThrow(any())).thenReturn(createApplication());
        when(applicationKeyService.getLastPublicKey(any())).thenReturn(Pair.of(1L, mockPublicKey));
        var key = jwtKeyController.getApplicationJwks(1L);

        assertNotNull(key);
        assertEquals("RSA", key.getKty());
        assertEquals("sig", key.getUse());
        assertEquals("1", key.getKid());
        assertEquals("RS256", key.getAlg());
        assertEquals(
                Base64.getUrlEncoder().withoutPadding().encodeToString(new BigInteger(1, new byte[]{1, 2, 3, 4}).toByteArray()),
                key.getN());
        assertEquals(
                Base64.getUrlEncoder().withoutPadding().encodeToString(BigInteger.valueOf(65537).toByteArray()),
                key.getE());
    }
}
