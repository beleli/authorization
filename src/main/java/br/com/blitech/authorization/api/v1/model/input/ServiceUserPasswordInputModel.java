package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ServiceUserPasswordInputModel extends ServiceUserInputModel implements Loggable {

    @NotBlank(message = "api.constraints.not-blank")
    @Length(max = 255, message = "api.constraints.length")
    @MaskProperty()
    @Schema(description = "User password", example = "admin123")
    private String password;

    public String getPassword() { return password; }

    public ServiceUserPasswordInputModel(String name, Long profileId, String password) {
        super(name, profileId);
        this.password = password;
    }
}
