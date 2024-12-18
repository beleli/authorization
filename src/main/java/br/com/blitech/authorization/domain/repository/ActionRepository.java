package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {

}
