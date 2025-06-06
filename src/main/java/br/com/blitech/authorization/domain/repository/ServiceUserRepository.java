package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.ServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ServiceUserRepository  extends JpaRepository<ServiceUser, Long> {
    Optional<ServiceUser> findByApplicationIdAndName(Long applicationId, String name);
    Optional<ServiceUser> findByApplicationIdAndId(Long applicationId, Long id);
    Set<ServiceUser> findByApplicationId(Long applicationId);
}

