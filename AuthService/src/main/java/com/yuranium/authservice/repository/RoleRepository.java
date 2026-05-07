package com.yuranium.authservice.repository;

import com.yuranium.authservice.enums.RoleType;
import com.yuranium.authservice.models.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer>
{
    Optional<RoleEntity> findByRole(RoleType role);
}