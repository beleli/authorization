package br.com.blitech.authorization.core.properties;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

@ConfigurationProperties("authorization.ldap")
public class LdapProperties {
    private String url;
    private String base;
    private String baseSearch;
    private Boolean pooled;
    private String userDn;
    private String password;

    public String getUrl() { return url; }
    public String getBase() { return base; }
    public String getBaseSearch() { return baseSearch; }
    public Boolean isPooled() { return pooled; }
    public String getUserDn() { return userDn; }
    public String getPassword() { return password; }

    public LdapProperties(String url, String base, String baseSearch, Boolean pooled, @NotNull Resource userDn, @NotNull Resource password) throws IOException {
        this.url = url;
        this.base = base;
        this.baseSearch = baseSearch;
        this.pooled = pooled;
        this.userDn = userDn.getContentAsString(Charset.defaultCharset());
        this.password = password.getContentAsString(Charset.defaultCharset());
    }
}
