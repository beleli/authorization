package br.com.blitech.authorization.infrastructure.domain;

import br.com.blitech.authorization.core.properties.LdapProperties;
import br.com.blitech.authorization.domain.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.stereotype.Component;

import javax.naming.NamingEnumeration;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class LdapAdapter implements DomainService {
    public static final String S_AM_ACCOUNT_NAME = "sAMAccountName";

    @Autowired
    private LdapProperties ldapProperties;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public Boolean authenticate(String username, String password) {
        Filter filter = new EqualsFilter(S_AM_ACCOUNT_NAME, username);
        return ldapTemplate.authenticate(ldapProperties.getBaseSearch(), filter.encode(), password);
    }

    @Override
    public List<String> findGroupsByUser(String username) {
        return ldapTemplate.search(
                query().base(ldapProperties.getBaseSearch()).where(S_AM_ACCOUNT_NAME).is(username),
                (AttributesMapper<List<String>>) attributes -> {
                    List<String> groups = new ArrayList<>();
                    NamingEnumeration<?> groupAttributes = attributes.get("memberOf").getAll();
                    while (groupAttributes.hasMore()) {
                        LdapName ldapName = new LdapName(groupAttributes.next().toString());
                        for (Rdn rdn: ldapName.getRdns()) {
                            if (rdn.getType().equalsIgnoreCase("cn")) {
                                groups.add(rdn.getValue().toString());
                                break;
                            }
                        }
                    }
                    return groups;
                }
        ).get(0);
    }
}
