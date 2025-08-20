package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ApplicationPasswordInputModel extends ApplicationInputModel implements Loggable {

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @MaskProperty()
    @Schema(description = "Application password", example = "admin123")
    private String password;

    public String getPassword() { return password; }

    public ApplicationPasswordInputModel(String name, String user, String password) {
        super(name, user);
        this.password = password;
    }
}
