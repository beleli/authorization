package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.LogMaskFormat;
import br.com.blitech.authorization.core.log.Loggable;
import br.com.blitech.authorization.core.log.MaskProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class LoginApplicationInputModel extends Loggable {

    @NotBlank
    @Length(max = 255)
    @MaskProperty(format = LogMaskFormat.NAME)
    private String username;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public LoginApplicationInputModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
