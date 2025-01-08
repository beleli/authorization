package br.com.blitech.authorization.api.exceptionhandler;

import br.com.blitech.authorization.api.exceptionhandler.ApiError.ApiFieldError;
import br.com.blitech.authorization.core.log.Loggable;
import br.com.blitech.authorization.core.message.Messages;
import br.com.blitech.authorization.domain.exception.BusinessException;
import br.com.blitech.authorization.domain.exception.EntityAlreadyExistsException;
import br.com.blitech.authorization.domain.exception.EntityInUseException;
import br.com.blitech.authorization.domain.exception.EntityNotFoundException;
import br.com.blitech.authorization.domain.exception.business.UserNotAuthorizedException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private Tracer tracer;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        return this.handleExceptionInternal(ex, getMessage("api.unscathed-exception"), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, null, null, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, null, null, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<Object> handleEntityInUseException(EntityInUseException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, null, null, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<Object> handleUserNotAuthorizedException(UserNotAuthorizedException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, null, null, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, null, null , HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> handlePropertyReferenceException(PropertyReferenceException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, getMessage("api.property-reference-exception"), null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(@NotNull ConstraintViolationException ex, WebRequest request) {
        Set<ApiFieldError> errors = ex.getConstraintViolations().stream()
            .map(constraintViolation ->
                new ApiFieldError(constraintViolation.getPropertyPath().toString(), getMessage(constraintViolation.getMessage()))
            ).collect(Collectors.toSet());
        var problemDetail = buildApiError(HttpStatus.BAD_REQUEST, getMessage("api.validation-exception"), request, errors);
        return this.handleExceptionInternal(ex, problemDetail, null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, ex.getReason(), null, ex.getStatusCode(), request);
    }

     @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return this.handleExceptionInternal(ex, null, null, HttpStatus.FORBIDDEN, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Set<ApiFieldError> errors = ex.getBindingResult().getAllErrors().stream()
            .map(objectError -> {
                var message = getMessage(objectError.getDefaultMessage());
                var name = objectError.getObjectName();
                if (objectError instanceof FieldError fieldError) { name = fieldError.getField(); }
                return new ApiFieldError(name, message);
            }
        ).collect(Collectors.toSet());
        var problemDetail = buildApiError(HttpStatus.BAD_REQUEST, getMessage("api.validation-exception"), request, errors);
        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(@NotNull HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var errors = new HashSet<ApiFieldError>();
        var rootCause = ex.getRootCause();
        if (ex.getRootCause() instanceof UnrecognizedPropertyException unrecognizedPropertyException) {
            errors.add(new ApiFieldError(unrecognizedPropertyException.getPropertyName(), getMessage("api.unrecognized-field-exception")));
        }
        var problemDetail = buildApiError(HttpStatus.BAD_REQUEST, getMessage("api.read-request-exception"), request, errors);
        return this.handleExceptionInternal(new Exception(rootCause), problemDetail, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Object newBody;
        if (body == null) {
            newBody = buildApiError(statusCode, ex.getLocalizedMessage(), request, null);
        } else if (body instanceof String message) {
            newBody = buildApiError(statusCode, message, request, null);
        } else {
            newBody = body;
        }

        var log = newBody instanceof Loggable loggable ? loggable.toJsonLog() : "not logged";
        logger.error(String.format("response httpStatus:%s, body:%s", statusCode.value(), log));
        return super.handleExceptionInternal(ex, newBody, headers, statusCode, request);
    }

    private String getMessage(String key, Object... args) {
        return Messages.get(key, args);
    }

    private ApiError buildApiError(@NotNull HttpStatusCode statusCode, String message, WebRequest request, Set<ApiFieldError> fieldApiErrors) {
        return new ApiError(
            HttpStatus.valueOf(statusCode.value()).getReasonPhrase(),
            statusCode.value(),
            message,
            URI.create(((ServletWebRequest) request).getRequest().getRequestURI()),
            Objects.requireNonNull(tracer.currentTraceContext().context()).traceId(),
            fieldApiErrors == null || fieldApiErrors.isEmpty() ? null : fieldApiErrors
        );
    }
}
