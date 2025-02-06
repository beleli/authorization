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
import io.swagger.v3.oas.models.security.SecurityRequirement;
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
    private static final String CLIENT_ERROR_RESPONSE = "ClientErrorResponse";

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
            ).addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> {
            pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                responses.addApiResponse("4xx", new ApiResponse().$ref(CLIENT_ERROR_RESPONSE));
            });
        });
    }

    @NotNull
    private Map<String, Schema> generateSchemas() {
        final Map<String, Schema> schemaMap = new HashMap<>();

        schemaMap.put("ApiError", ModelConverters.getInstance().read(ApiError.class).get("ApiError"));
        schemaMap.put("ApiFieldError", ModelConverters.getInstance().read(ApiError.ApiFieldError.class).get("ApiFieldError"));

        return schemaMap;
    }

    @NotNull
    private Map<String, ApiResponse> generateResponses() {
        final Map<String, ApiResponse> apiResponseMap = new HashMap<>();
        Content content = new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(new Schema<ApiError>().$ref("ApiError")));

        apiResponseMap.put(CLIENT_ERROR_RESPONSE, new ApiResponse().description("Client Error").content(content));

        return apiResponseMap;
    }
}