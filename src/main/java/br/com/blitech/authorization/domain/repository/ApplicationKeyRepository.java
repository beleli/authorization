package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.ApplicationKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationKeyRepository extends JpaRepository<ApplicationKey, Long> {
    Long countByApplicationId(Long applicationId);
    List<ApplicationKey> findByApplicationIdOrderByIdDesc(Long applicationId);
}
