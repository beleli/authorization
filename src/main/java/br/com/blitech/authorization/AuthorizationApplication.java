package br.com.blitech.authorization;

import br.com.blitech.authorization.core.io.Base64ProtocolResolver;
import br.com.blitech.authorization.core.io.SecretsManagerProtocolResolver;
import br.com.blitech.authorization.core.io.StringProtocolResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthorizationApplication {

    public static void main(String[] args) {
        var app = new SpringApplication(AuthorizationApplication.class);
        app.addListeners(new StringProtocolResolver(), new Base64ProtocolResolver(), new SecretsManagerProtocolResolver());
        app.run(args);
    }
}
