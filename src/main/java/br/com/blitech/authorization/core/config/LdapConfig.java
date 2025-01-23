package br.com.blitech.authorization.core.config;

import br.com.blitech.authorization.core.properties.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(LdapProperties.class)
public class LdapConfig {

    @Autowired
    private LdapProperties ldapProperties;

    @Bean
    public LdapContextSource ldapContextSource() throws IOException {
        var ldapContextSource = new LdapContextSource();

        ldapContextSource.setUrl(ldapProperties.getUrl());
        ldapContextSource.setBase(ldapProperties.getBase());
        ldapContextSource.setPooled(ldapProperties.isPooled());
        ldapContextSource.setUserDn(ldapProperties.getUserDn());
        ldapContextSource.setPassword(ldapProperties.getPassword());

        return ldapContextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}
