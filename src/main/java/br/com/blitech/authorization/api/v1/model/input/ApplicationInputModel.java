package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ApplicationInputModel implements Loggable {

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Application name", example = "My Application")
    private String name;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    @Schema(description = "Application token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
    private String token;

    @NotBlank
    @Length(max = 255)
    @Schema(description = "Application user", example = "admin")
    private String user;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    @Schema(description = "Application password", example = "admin123")
    private String password;

    public String getName() { return name; }
    public String getToken() { return token; }
    public String getUser() { return user; }
    public String getPassword() { return password; }

    public ApplicationInputModel(String name, String token, String user, String password) {
        this.name = name;
        this.token = token;
        this.user = user;
        this.password = password;
    }
}
