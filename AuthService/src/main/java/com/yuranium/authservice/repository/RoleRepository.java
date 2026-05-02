package com.yuranium.authservice.repository;

import com.yuranium.authservice.models.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {}