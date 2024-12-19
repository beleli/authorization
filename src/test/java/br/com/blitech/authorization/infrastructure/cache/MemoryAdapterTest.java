package br.com.blitech.authorization.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAdapterTest {

    private MemoryAdapter memoryAdapter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        memoryAdapter = new MemoryAdapter(objectMapper);
    }

    @Test
    void testSetAndGetKey() {
        String key = "testKey";
        String value = "testValue";
        Duration duration = Duration.ofSeconds(60);

        memoryAdapter.setKey(key, value, duration);
        String result = memoryAdapter.getKey(key, String.class);

        assertEquals(value, result);
    }

    @Test
    void testGetKeyExpired() throws InterruptedException {
        String key = "testKey";
        String value = "testValue";
        Duration duration = Duration.ofSeconds(1);

        memoryAdapter.setKey(key, value, duration);
        Thread.sleep(2000); // Wait for the key to expire
        String result = memoryAdapter.getKey(key, String.class);

        assertNull(result);
    }

    @Test
    void testGetKeyNotFound() {
        String result = memoryAdapter.getKey("nonExistentKey", String.class);
        assertNull(result);
    }

    @Test
    void testSetKeySerializationError() {
        String key = "testKey";
        Object value = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Serialization error");
            }
        };
        Duration duration = Duration.ofSeconds(60);

        assertThrows(RuntimeException.class, () -> memoryAdapter.setKey(key, value, duration));
    }
}