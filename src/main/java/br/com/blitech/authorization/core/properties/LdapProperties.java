package br.com.blitech.authorization.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("authorization.ldap")
public class LdapProperties {
    private String url;
    private String base;
    private String baseSearch;
    private Boolean pooled;
    private Resource userDn;
    private Resource password;

    public String getUrl() { return url; }
    public String getBase() { return base; }
    public String getBaseSearch() { return baseSearch; }
    public Boolean getPooled() { return pooled; }
    public Resource getUserDn() { return userDn; }
    public Resource getPassword() { return password; }

    public void setUrl(String url) { this.url = url; }
    public void setBase(String base) { this.base = base; }
    public void setBaseSearch(String baseSearch) { this.baseSearch = baseSearch; }
    public void setPooled(Boolean pooled) { this.pooled = pooled; }
    public void setUserDn(Resource userDn) { this.userDn = userDn; }
    public void setPassword(Resource password) { this.password = password; }
}
