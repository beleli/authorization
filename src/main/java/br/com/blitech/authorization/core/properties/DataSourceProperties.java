package br.com.blitech.authorization.core.properties;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

@ConfigurationProperties("authorization.datasource")
public class DataSourceProperties {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public DataSourceProperties(@NotNull Resource username, @NotNull Resource password) throws IOException {
        this.username = username.getContentAsString(Charset.defaultCharset());
        this.password = password.getContentAsString(Charset.defaultCharset());
    }
}
