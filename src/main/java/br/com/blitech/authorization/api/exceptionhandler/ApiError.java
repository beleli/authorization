package br.com.blitech.authorization.api.exceptionhandler;

import br.com.blitech.authorization.core.log.Loggable;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError extends Loggable {
    private final String title;
    private final Integer status;
    private final String detail;
    private final URI instance;
    private final String traceId;
    private final Set<ApiFieldError> errors;

    public String getTitle() { return title; }
    public Integer getStatus() { return status; }
    public String getDetail() { return detail; }
    public URI getInstance() { return instance; }
    public String getTraceId() { return traceId; }
    public Set<ApiFieldError> getErrors() { return errors; }

    public ApiError(String title, Integer status, String detail, URI instance, String traceId, Set<ApiFieldError> errors) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.traceId = traceId;
        this.errors = errors;
    }

    public record ApiFieldError(String field, String error) { }
}
