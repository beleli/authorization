package br.com.blitech.authorization.domain.service;

import java.util.List;

public interface DomainService {
    Boolean authenticate(String username, String password);
    List<String> findGroupsByUser(String username);
}
