package com.artemis.authservice.repository;

import com.artemis.authservice.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>
{
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByIdAndIsDeletedFalse(Long id);

    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);

    List<UserEntity> findAllByIsDeletedFalse();
}