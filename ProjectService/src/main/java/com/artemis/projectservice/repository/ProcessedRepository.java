package com.artemis.projectservice.repository;

import com.artemis.projectservice.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessedRepository extends JpaRepository<ProcessedEventEntity, Long>
{
    Optional<ProcessedEventEntity> findByMessageId(UUID messageId);
}