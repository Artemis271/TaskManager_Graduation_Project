package com.yuranium.projectservice.repository;

import com.yuranium.projectservice.entity.AvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<AvatarEntity, Long> {}
