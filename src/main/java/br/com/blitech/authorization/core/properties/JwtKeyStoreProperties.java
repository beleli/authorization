package br.com.blitech.authorization.core.properties;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

@ConfigurationProperties("authorization.jwt-keystore")
public class JwtKeyStoreProperties {

    private Resource jksLocation;
    private String keypairAlias;
    private String password;

    public Resource getJksLocation() { return jksLocation; }
    public String getKeypairAlias() { return keypairAlias; }
    public String getPassword() { return password; }

    public JwtKeyStoreProperties(Resource jksLocation, @NotNull Resource keypairAlias, @NotNull Resource password) throws IOException {
        this.jksLocation = jksLocation;
        this.keypairAlias = keypairAlias.getContentAsString(Charset.defaultCharset());
        this.password = password.getContentAsString(Charset.defaultCharset());
    }
}
