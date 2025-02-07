package br.com.blitech.authorization.core.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggableTest {

    private TestLoggable testLoggable;

    @BeforeEach
    void setUp() {
        testLoggable = new TestLoggable();
    }

    @Test
    void testToLog() {
        String log = testLoggable.toLog();
        assertNotNull(log);
        assertTrue(log.contains("TestLoggable"));
        assertTrue(log.contains("name=Jo** Do*"));
        assertTrue(log.contains("email=j*******@e******.com"));
        assertTrue(log.contains("address=135 B*****"));
        assertTrue(log.contains("cpf=123*****01"));
        assertTrue(log.contains("token=*****"));
    }

    @Test
    void testToJsonLog() {
        String jsonLog = testLoggable.toJsonLog();
        assertNotNull(jsonLog);
        assertTrue(jsonLog.contains("\"name\":\"Jo** Do*\""));
        assertTrue(jsonLog.contains("\"email\":\"j*******@e******.com\""));
        assertTrue(jsonLog.contains("\"email\":\"j*******@e******.com\""));
        assertTrue(jsonLog.contains("\"address\":\"135 B*****\""));
        assertTrue(jsonLog.contains("\"token\":\"*****\""));
    }

    static class TestLoggable implements Loggable {
        @MaskProperty(format = LogMaskFormat.NAME)
        private String name = "John Doe";
        @MaskProperty(format = LogMaskFormat.EMAIL)
        private String email = "john.doe@example.com";
        @MaskProperty(format = LogMaskFormat.ADDRESS)
        private String address = "135 Brompton Road";
        @MaskProperty(format = LogMaskFormat.CPF)
        private String cpf = "12345678901";
        @MaskProperty
        private String token = "token";
        private OffsetDateTime timestamp = OffsetDateTime.now();
    }
}