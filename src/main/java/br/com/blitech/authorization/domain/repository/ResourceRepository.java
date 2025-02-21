package br.com.blitech.authorization.domain.repository;

import br.com.blitech.authorization.domain.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> { }
