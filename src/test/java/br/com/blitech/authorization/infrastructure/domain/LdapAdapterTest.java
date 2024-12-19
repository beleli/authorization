package br.com.blitech.authorization.infrastructure.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LdapAdapterTest {

    @Mock
    private LdapTemplate ldapTemplate;

    @Value("${ldap.context.source.baseSearch}")
    private String baseSearch;

    @InjectMocks
    private LdapAdapter ldapAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticate() {
        String username = "testUser";
        String password = "testPassword";
        EqualsFilter filter = new EqualsFilter(LdapAdapter.S_AM_ACCOUNT_NAME, username);

        when(ldapTemplate.authenticate(baseSearch, filter.encode(), password)).thenReturn(true);
        assertTrue(ldapAdapter.authenticate(username, password));

        when(ldapTemplate.authenticate(baseSearch, filter.encode(), password)).thenReturn(false);
        assertFalse(ldapAdapter.authenticate(username, password));
    }

   /*@Test
    void testFindGroupsByUser() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        Attributes attributes = new BasicAttributes();
        attributes.put("cn", groupName);
        NamingEnumeration<?> namingEnumeration = mock(NamingEnumeration.class);
        when(namingEnumeration.hasMore()).thenReturn(true, false);
        when(namingEnumeration.next()).thenReturn(new LdapName("cn=" + groupName + ",ou=groups,dc=example,dc=com"));

        when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenAnswer(invocation -> {
            AttributesMapper<List<String>> mapper = invocation.getArgument(2);
            return Collections.singletonList(mapper.mapFromAttributes(attributes));
        });

        List<String> groups = ldapAdapter.findGroupsByUser(username);
        assertEquals(1, groups.size());
        assertEquals(groupName, groups.get(0));
    }*/
}