package br.com.blitech.authorization.api.exceptionhandler;

import br.com.blitech.authorization.core.message.Messages;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Tracer tracer;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ApiAuthenticationEntryPoint(Tracer tracer, ObjectMapper objectMapper) {
        this.tracer = tracer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        logger.info("request uri:{}, httpMethod:{}, body:not logged", request.getRequestURI(), request.getMethod());

        var status = HttpStatus.UNAUTHORIZED;
        var problemDetail = new ApiProblemDetail(
            status.getReasonPhrase(),
            status.value(),
            Messages.get("api.unauthorized-exception"),
            URI.create(request.getRequestURI()),
            Objects.requireNonNull(tracer.currentTraceContext().context()).traceId(),
            null
        );

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));

        logger.error("response httpStatus:{}, body:{}", status.value(), problemDetail.toJsonLog());
    }
}
