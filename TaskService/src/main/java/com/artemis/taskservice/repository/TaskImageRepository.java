package com.artemis.taskservice.repository;

import com.artemis.taskservice.entity.TaskImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskImageRepository extends JpaRepository<TaskImageEntity, Long> {}