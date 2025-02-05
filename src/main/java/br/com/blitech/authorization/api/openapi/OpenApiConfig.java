package br.com.blitech.authorization.api.openapi;

import br.com.blitech.authorization.api.exceptionhandler.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class OpenApiConfig {

    private static final String badRequestResponse = "BadRequestResponse";
    private static final String notFoundResponse = "NotFoundResponse";
    private static final String notAcceptableResponse = "NotAcceptableResponse";
    private static final String internalServerErrorResponse = "InternalServerErrorResponse";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("BliTech Authorization API")
                    .version("v1")
                    .description("REST API of Authorization")
                    .license(new License().name("BliTech Corp").url("http://www.blitech.com.br"))
                ).tags(Arrays.asList(
                    new Tag().name("Login").description("Provide Authentication Token"),
                    new Tag().name("Actions").description("Actions Management"),
                    new Tag().name("Applications").description("Applications Management"),
                    new Tag().name("Profiles").description("Profiles Management"),
                    new Tag().name("Resources").description("Resources Management"),
                    new Tag().name("JwtKey").description("Provide JWT Public Key")
                )).components(new Components()
                    .schemas(generateSchemas())
                    .responses(generateResponses())
                );
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperationsMap()
                .forEach((httpMethod, operation) -> {
                    ApiResponses responses = operation.getResponses();
                    switch (httpMethod) {
                        case GET:
                            responses.addApiResponse("406", new ApiResponse().$ref(notAcceptableResponse));
                            break;
                        case POST, DELETE:
                            responses.addApiResponse("400", new ApiResponse().$ref(badRequestResponse));
                            responses.addApiResponse("404", new ApiResponse().$ref(notFoundResponse));
                            break;
                        case PUT:
                            responses.addApiResponse("400", new ApiResponse().$ref(badRequestResponse));
                            break;
                        default:
                            break;
                    }
                    responses.addApiResponse("500", new ApiResponse().$ref(internalServerErrorResponse));
                })
            );
        };
    }

    @NotNull
    private Map<String, Schema> generateSchemas() {
        final Map<String, Schema> schemaMap = new HashMap<>();

        schemaMap.putAll(ModelConverters.getInstance().read(ApiError.class));
        schemaMap.putAll(ModelConverters.getInstance().read(ApiError.ApiFieldError.class));

        return schemaMap;
    }

    @NotNull
    private Map<String, ApiResponse> generateResponses() {
        final Map<String, ApiResponse> apiResponseMap = new HashMap<>();

        Content content = new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(new Schema<ApiError>().$ref("ApiError")));

        apiResponseMap.put(badRequestResponse, new ApiResponse().description("Bad Request").content(content));
        apiResponseMap.put(notFoundResponse, new ApiResponse().description("Not Found").content(content));
        apiResponseMap.put(notAcceptableResponse, new ApiResponse().description("Not Acceptable").content(content));
        apiResponseMap.put(internalServerErrorResponse, new ApiResponse().description("Internal Server Error").content(content));

        return apiResponseMap;
    }
}
