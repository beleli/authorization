package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByApplicationIdAndId(Long applicationId, Long id);
}
