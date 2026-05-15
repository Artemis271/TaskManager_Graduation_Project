package com.artemis.authservice.repository;

import com.artemis.authservice.models.entity.AvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<AvatarEntity, Long> {}