package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByNameIgnoreCase(String name);
}
