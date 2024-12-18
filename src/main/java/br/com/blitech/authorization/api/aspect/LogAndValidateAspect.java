package br.com.blitech.authorization.api.aspect;

import br.com.blitech.authorization.api.utlis.ResourceUriHelper;
import br.com.blitech.authorization.core.log.Loggable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

@Aspect
@Component
public class LogAndValidateAspect {
    private final Map<Class<?>, Logger> loggers = new HashMap<>();
    private final Validator validator = buildDefaultValidatorFactory().getValidator();

    @Pointcut("@annotation(logAndValidate)")
    public void logMethods(LogAndValidate logAndValidate) {
        // Pointcut definition
    }

    @Before("logMethods(logAndValidate)")
    public void logRequest(JoinPoint joinPoint, LogAndValidate logAndValidate) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            getLogger(joinPoint).warn("Request attributes are null; cannot log request.");
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        var requestBody = getRequestBody(joinPoint);
        if (requestBody == null) {
            Object parameter = joinPoint.getArgs().length > 0 ? joinPoint.getArgs()[0] : null;
            var log = parameter instanceof Pageable ? parameter.toString() : "body:null";
            logRequest(getLogger(joinPoint), request, log);
        } else {
            var log = createLog(requestBody);
            logRequest(getLogger(joinPoint), request, "body:" + log);

            if (logAndValidate.validateRequest()) {
                validateRequestBody(requestBody, logAndValidate.validationGroups());
            }
        }
    }

    private static String createLog(Object object) {
        return object instanceof Loggable loggable ? loggable.toJsonLog() : "not logged";
    }

    private void logRequest(@NotNull Logger logger, HttpServletRequest request, String log) {
        String username = ResourceUriHelper.getUsername(request);
        String uri = request.getRequestURI();
        String httpMethod = request.getMethod();
        logger.info("request username:{}, uri:{}, httpMethod:{}, {}", username, uri, httpMethod, log);
    }

    @AfterReturning(value = "logMethods(logAndValidate)", returning = "returnValue")
    public void logResponse(@NotNull JoinPoint joinPoint, @NotNull LogAndValidate logAndValidate, Object returnValue) {
        int status = returnValue instanceof ResponseEntity<?>
                ? ((ResponseEntity<?>) returnValue).getStatusCode().value()
                : getResponseStatus(joinPoint);

        Object body = returnValue instanceof ResponseEntity<?>
                ? ((ResponseEntity<?>) returnValue).getBody()
                : returnValue;

        var log = body == null ? null : createLog(body);
        getLogger(joinPoint).info("response httpStatus:{}, body:{}", status, log);
    }

    private Logger getLogger(@NotNull JoinPoint joinPoint) {
        Class<?> resourceClass = joinPoint.getTarget().getClass();
        return loggers.computeIfAbsent(resourceClass, LoggerFactory::getLogger);
    }

    private int getResponseStatus(@NotNull JoinPoint joinPoint) {
        ResponseStatus responseStatusAnnotation = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(ResponseStatus.class);
        return responseStatusAnnotation != null
                ? responseStatusAnnotation.value().value()
                : HttpStatus.OK.value();
    }

    @Nullable
    private Object getRequestBody(@NotNull JoinPoint joinPoint) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var parameters = methodSignature.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                return joinPoint.getArgs()[i];
            }
        }
        return null;
    }

    private void validateRequestBody(Object requestBody, Class<?>[] validationGroups) {
        Set<ConstraintViolation<Object>> violations = validator.validate(requestBody, validationGroups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
