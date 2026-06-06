package com.artemis.projectservice.repository;

import com.artemis.projectservice.entity.AvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<AvatarEntity, Long> {}
