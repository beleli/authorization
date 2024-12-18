package br.com.blitech.authorization.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("authorization.jwt-keystore")
public class JwtKeyStoreProperties {

    private Resource jksLocation;
    private Resource keypairAlias;
    private Resource password;

    public Resource getJksLocation() { return jksLocation; }
    public Resource getKeypairAlias() { return keypairAlias; }
    public Resource getPassword() { return password; }

    public JwtKeyStoreProperties(Resource jksLocation, Resource keypairAlias, Resource password) {
        this.jksLocation = jksLocation;
        this.keypairAlias = keypairAlias;
        this.password = password;
    }
}
