package br.com.blitech.authorization.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisAdapter redisAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisAdapter = new RedisAdapter(redisTemplate, new ObjectMapper());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSetAndGetKey() {
        String key = "testKey";
        String value = "testValue";
        Duration duration = Duration.ofSeconds(60);

        redisAdapter.setKey(key, value, duration);
        verify(valueOperations).set(key, "\"testValue\"", duration);

        when(valueOperations.get(key)).thenReturn("\"testValue\"");
        String result = redisAdapter.getKey(key, String.class);
        assertEquals(value, result);
    }

    @Test
    void testSetAndGetKeyIntegerValue() {
        String key = "testKey";
        Integer value = 42;
        Duration duration = Duration.ofSeconds(60);

        redisAdapter.setKey(key, value, duration);
        verify(valueOperations).set(key, "42", duration);

        when(valueOperations.get(key)).thenReturn("42");
        Integer result = redisAdapter.getKey(key, Integer.class);
        assertEquals(value, result);
    }

    @Test
    void testSetAndGetKeyObjectValue() {
        String key = "testKey";
        Sample value = new Sample("name", 10);
        Duration duration = Duration.ofSeconds(60);

        redisAdapter.setKey(key, value, duration);
        verify(valueOperations).set(key, "{\"name\":\"name\",\"count\":10}", duration);

        when(valueOperations.get(key)).thenReturn("{\"name\":\"name\",\"count\":10}");
        Sample result = redisAdapter.getKey(key, Sample.class);
        assertEquals(value, result);
    }

    @Test
    void testSetNullValue() {
        String key = "testKey";
        Duration duration = Duration.ofSeconds(60);

        assertThrows(IllegalArgumentException.class, () -> redisAdapter.setKey(key, null, duration));
        verifyNoInteractions(valueOperations);
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
        Object value = new Object();
        Duration duration = Duration.ofSeconds(60);

        assertThrows(IllegalStateException.class, () -> redisAdapter.setKey(key, value, duration));
        verifyNoInteractions(valueOperations);
    }

    @Test
    void testGetKeyDeserializationError() {
        String key = "testKey";
        when(valueOperations.get(key)).thenReturn("invalidJson");

        assertThrows(IllegalStateException.class, () -> redisAdapter.getKey(key, Object.class));
    }

    record Sample(String name, int count) { }
}
