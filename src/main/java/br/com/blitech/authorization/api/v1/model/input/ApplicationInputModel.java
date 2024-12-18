package br.com.blitech.authorization.api.v1.model.input;

import br.com.blitech.authorization.core.log.Loggable;
import br.com.blitech.authorization.core.log.MaskProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class ApplicationInputModel extends Loggable {

    @NotBlank
    @Length(max = 255)
    private String name;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    private String token;

    @NotBlank
    @Length(max = 255)
    private String user;

    @NotBlank
    @Length(max = 255)
    @MaskProperty()
    private String password;

    public String getName() { return name; }
    public String getToken() { return token; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
}
