package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class LoginInputModel implements Loggable {

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @MaskProperty(format = LogMaskFormat.NAME)
    @Schema(description = "User name", example = "admin")
    private String username;

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @MaskProperty()
    @Schema(description = "User password", example = "admin123")
    private String password;

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @MaskProperty()
    @Schema(description = "Application name", example = "My Application")
    private String application;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getApplication() { return application; }

    public LoginInputModel(String username, String password, String application) {
        this.username = username;
        this.password = password;
        this.application = application;
    }
}
