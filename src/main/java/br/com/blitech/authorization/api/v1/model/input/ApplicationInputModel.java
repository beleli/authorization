package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ApplicationInputModel implements Loggable {

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @Schema(description = "Application name", example = "My Application")
    private String name;

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @Schema(description = "Application user", example = "admin")
    private String user;

    public String getName() { return name; }
    public String getUser() { return user; }

    public ApplicationInputModel(String name, String user) {
        this.name = name;
        this.user = user;
    }
}
