package br.com.blitech.authorization.api.v1.model;

import br.com.blitech.authorization.core.log.Loggable;
import br.com.blitech.authorization.core.log.MaskProperty;

public class LoginModel extends Loggable {

    @MaskProperty
    private String token;

    public LoginModel(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}
