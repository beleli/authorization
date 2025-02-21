package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ChangePasswordInputModel implements Loggable {

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    @Schema(description = "Current password", example = "admin123")
    private String password;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    @Schema(description = "New password", example = "admin456")
    private String newPassword;

    public String getPassword() { return password; }
    public String getNewPassword() { return newPassword; }

    public ChangePasswordInputModel(String password, String newPassword) {
        this.password = password;
        this.newPassword = newPassword;
    }
}
