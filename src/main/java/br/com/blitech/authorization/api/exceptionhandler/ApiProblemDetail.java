package br.com.blitech.authorization.api.exceptionhandler;

import br.com.blitech.authorization.core.log.Loggable;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiProblemDetail", description = "Details about an API error")
public class ApiProblemDetail implements Loggable {

    @Schema(example = "Bad Request")
    private final String title;

    @Schema(example = "400")
    private final Integer status;

    @Schema(example = "Invalid field")
    private final String detail;

    @Schema(example = "http://localhost:8080/api/v1/applications")
    private final URI instance;

    @Schema(example = "67a37ec622f5fd688db6150bd58b4a26")
    private final String traceId;

    @Schema(description = "List of objects or fields that generated the error")
    private final Set<ApiFieldError> errors;

    public String getTitle() { return title; }
    public Integer getStatus() { return status; }
    public String getDetail() { return detail; }
    public URI getInstance() { return instance; }
    public String getTraceId() { return traceId; }
    public Set<ApiFieldError> getErrors() { return errors; }

    public ApiProblemDetail(String title, Integer status, String detail, URI instance, String traceId, Set<ApiFieldError> errors) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.traceId = traceId;
        this.errors = errors;
    }

    @Schema(name = "ApiFieldError", description = "Details about a specific field error")
    public record ApiFieldError (
        @Schema(example = "name")
        String field,
        @Schema(example = "must not be blank")
        String error
    ){ }
}
