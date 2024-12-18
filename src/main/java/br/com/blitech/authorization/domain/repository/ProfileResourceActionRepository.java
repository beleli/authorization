package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Application;
import br.com.blitech.authorization.domain.entity.Profile;
import br.com.blitech.authorization.domain.entity.ProfileResourceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfileResourceActionRepository extends JpaRepository<ProfileResourceAction, Long> {
    @Query("select p from ProfileResourceAction p where p.profile.application.id = :applicationId and p.profile.group in :groups")
    List<ProfileResourceAction> findByApplicationAndGroups(Long applicationId, List<String> groups);
    List<ProfileResourceAction> findByProfileApplication(Application application);
    List<ProfileResourceAction> findByProfile(Profile profile);
}
