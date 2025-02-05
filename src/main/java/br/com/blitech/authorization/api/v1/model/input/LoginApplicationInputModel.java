package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.LogMaskFormat;
import br.com.blitech.authorization.core.log.Loggable;
import br.com.blitech.authorization.core.log.MaskProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class LoginApplicationInputModel extends Loggable {

    @NotBlank
    @Length(max = 255)
    @MaskProperty(format = LogMaskFormat.NAME)
    @Schema(description = "Application user", example = "admin")
    private String username;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    @Schema(description = "Application password", example = "admin123")
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public LoginApplicationInputModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
