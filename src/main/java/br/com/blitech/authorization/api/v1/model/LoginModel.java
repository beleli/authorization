package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;

public class LoginModel extends Loggable {

    @MaskProperty
    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
    private String token;

    public LoginModel(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}
