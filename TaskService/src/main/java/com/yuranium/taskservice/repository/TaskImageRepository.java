package com.yuranium.taskservice.repository;

import com.yuranium.taskservice.entity.TaskImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskImageRepository extends JpaRepository<TaskImageEntity, Long> {}