package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class ApplicationInputModel implements Loggable {

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Application name", example = "My Application")
    private String name;

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Application user", example = "admin")
    private String user;

    @NotNull
    @Schema(description = "Use default key", example = "true")
    private Boolean useDefaultKey;

    public String getName() { return name; }
    public String getUser() { return user; }
    public Boolean getUseDefaultKey() { return useDefaultKey; }

    public ApplicationInputModel(String name, String user, Boolean useDefaultKey) {
        this.name = name;
        this.user = user;
        this.useDefaultKey = useDefaultKey;
    }
}
