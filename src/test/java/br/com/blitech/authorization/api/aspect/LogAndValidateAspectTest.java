package br.com.blitech.authorization.api.aspect;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogAndValidateAspectTest {

    @Mock
    private Validator validator;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Logger logger;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @InjectMocks
    private LogAndValidateAspect logAndValidateAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(this.getClass().getDeclaredMethods()[0]);
    }

    @Test
    void testLogRequestWithoutRequestAttributes() {
        when(RequestContextHolder.getRequestAttributes()).thenReturn(null);

        logAndValidateAspect.logRequest(joinPoint, mock(LogAndValidate.class));

        verify(logger).warn("Request attributes are null; cannot log request.");
    }

    @Test
    void testLogRequestWithRequestAttributes() {
        when(RequestContextHolder.getRequestAttributes()).thenReturn(servletRequestAttributes);
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(ResourceUriHelper.getUsername(request)).thenReturn("testUser");

        Object[] args = new Object[]{};
        when(joinPoint.getArgs()).thenReturn(args);

        logAndValidateAspect.logRequest(joinPoint, mock(LogAndValidate.class));

        verify(logger).info("request username:{}, uri:{}, httpMethod:{}, {}", "testUser", "/test", "GET", "body:null");
    }

    @Test
    void testLogResponse() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("response", HttpStatus.OK);

        logAndValidateAspect.logResponse(joinPoint, mock(LogAndValidate.class), responseEntity);

        verify(logger).info("response httpStatus:{}, body:{}", HttpStatus.OK.value(), "not logged");
    }
}