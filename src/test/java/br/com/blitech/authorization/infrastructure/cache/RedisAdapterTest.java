package br.com.blitech.authorization.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisAdapterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private ObjectMapper objectMapper;
    private RedisAdapter redisAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        redisAdapter = new RedisAdapter(redisTemplate, objectMapper);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSetAndGetKey() {
        String key = "testKey";
        String value = "testValue";
        Duration duration = Duration.ofSeconds(60);

        redisAdapter.setKey(key, value, duration);
        verify(valueOperations).set(eq(key), eq(value), eq(duration));

        when(valueOperations.get(key)).thenReturn(value);
        String result = redisAdapter.getKey(key, String.class);
        assertEquals(value, result);
    }

    @Test
    void testSetAndGetKeyIntegerValue() {
        String key = "testKey";
        Integer value = 42;
        Duration duration = Duration.ofSeconds(60);

        redisAdapter.setKey(key, value, duration);
        verify(valueOperations).set(eq(key), eq(value.toString()), eq(duration));

        when(valueOperations.get(key)).thenReturn(value.toString());
        Integer result = redisAdapter.getKey(key, Integer.class);
        assertEquals(value, result);
    }

    @Test
    void testSetNullValue() {
        String key = "testKey";
        Object value = null;
        Duration duration = Duration.ofSeconds(60);

        assertThrows(IllegalArgumentException.class, () -> redisAdapter.setKey(key, value, duration));
    }

    @Test
    void testGetKeyNotFound() {
        String key = "nonExistentKey";
        when(valueOperations.get(key)).thenReturn(null);

        String result = redisAdapter.getKey(key, String.class);
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

        assertThrows(RuntimeException.class, () -> redisAdapter.setKey(key, value, duration));
    }

    @Test
    void testGetKeyDeserializationError() {
        String key = "testKey";
        String value = "invalidJson";
        when(valueOperations.get(key)).thenReturn(value);

        assertThrows(RuntimeException.class, () -> redisAdapter.getKey(key, Object.class));
    }
}