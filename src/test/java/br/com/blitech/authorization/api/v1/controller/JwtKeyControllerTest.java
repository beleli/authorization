package br.com.blitech.authorization.api.v1.controller;

import br.com.blitech.authorization.api.security.JwtKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtKeyControllerTest {

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
        when(jwtKeyProvider.getKeyId()).thenReturn("test-key-id");

        when(mockPublicKey.getModulus()).thenReturn(new BigInteger(1, new byte[]{1, 2, 3, 4}));
        when(mockPublicKey.getPublicExponent()).thenReturn(BigInteger.valueOf(65537));
    }

    @Test
    void testGetJwks() {
        var result = jwtKeyController.getJwks();

        assertNotNull(result);
        assertTrue(result.containsKey("keys"));

        var keys = (Iterable<?>) result.get("keys");
        var iterator = keys.iterator();
        assertTrue(iterator.hasNext());

        var key = (Map<String, Object>) iterator.next();
        assertEquals("RSA", key.get("kty"));
        assertEquals("test-key-id", key.get("kid"));
        assertEquals("RS256", key.get("alg"));
        assertEquals("sig", key.get("use"));
        assertEquals(
                Base64.getUrlEncoder().withoutPadding().encodeToString(new BigInteger(1, new byte[]{1, 2, 3, 4}).toByteArray()),
                key.get("n"));
        assertEquals(
                Base64.getUrlEncoder().withoutPadding().encodeToString(BigInteger.valueOf(65537).toByteArray()),
                key.get("e"));
    }
}
