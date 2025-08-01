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
        assertTrue(log.contains("name=Ca**** Be****"));
        assertTrue(log.contains("email=b*****@g****.com"));
        assertTrue(log.contains("address=135 B*****"));
        assertTrue(log.contains("cpf=123*****01"));
        assertTrue(log.contains("token=*****"));
    }

    @Test
    void testToJsonLog() {
        String jsonLog = testLoggable.toJsonLog();
        assertNotNull(jsonLog);
        assertTrue(jsonLog.contains("\"name\":\"Ca**** Be****\""));
        assertTrue(jsonLog.contains("\"email\":\"b*****@g****.com\""));
        assertTrue(jsonLog.contains("\"address\":\"135 B*****\""));
        assertTrue(jsonLog.contains("\"token\":\"*****\""));
    }

    static class TestLoggable implements Loggable {
        @MaskProperty(format = LogMaskFormat.NAME)
        private String name = "Carlos Beleli";
        @MaskProperty(format = LogMaskFormat.EMAIL)
        private String email = "beleli@gmail.com";
        @MaskProperty(format = LogMaskFormat.ADDRESS)
        private String address = "135 Brompton Road";
        @MaskProperty(format = LogMaskFormat.CPF)
        private String cpf = "12345678901";
        @MaskProperty
        private String token = "token";
        private OffsetDateTime timestamp = OffsetDateTime.now();
    }
}