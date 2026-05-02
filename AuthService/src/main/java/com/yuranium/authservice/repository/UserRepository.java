package com.yuranium.authservice.repository;

import com.yuranium.authservice.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>
{
    Optional<UserEntity> findByEmail(String email);
}