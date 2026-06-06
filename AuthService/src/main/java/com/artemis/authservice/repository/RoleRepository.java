package com.artemis.authservice.repository;

import com.artemis.authservice.enums.RoleType;
import com.artemis.authservice.models.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>
{
    Optional<RoleEntity> findByRole(RoleType role);
}