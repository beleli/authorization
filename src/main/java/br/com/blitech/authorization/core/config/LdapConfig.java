package br.com.blitech.authorization.core.config;

import br.com.blitech.authorization.core.properties.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableConfigurationProperties(LdapProperties.class)
public class LdapConfig {

    @Autowired
    private LdapProperties ldapProperties;

    @Bean
    public LdapContextSource ldapContextSource() {
        var ldapContextSource = new LdapContextSource();

        ldapContextSource.setUrl(ldapProperties.getUrl());
        ldapContextSource.setBase(ldapProperties.getBase());
        ldapContextSource.setPooled(ldapProperties.getPooled());
        ldapContextSource.setUserDn(ldapProperties.getUserDn().toString());
        ldapContextSource.setPassword(ldapProperties.getPassword().toString());

        return ldapContextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}
