package com.yuranium.taskservice.repository;

import com.yuranium.taskservice.entity.TaskEntity;
import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID>
{
    List<TaskEntity> findAllByProjectId(UUID projectId, Pageable pageable);

    @Query("FROM TaskEntity t WHERE t.projectId IN(:uuids)")
    List<TaskEntity> findByProjectIds(List<UUID> uuids);

    List<TaskEntity> findByName(String name, Pageable pageable);

    List<TaskEntity> findByTaskImportance(TaskImportance importance, Pageable pageable);

    List<TaskEntity> findByTaskStatus(TaskStatus status, Pageable pageable);

    void deleteAllByProjectId(UUID id);

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.projectId IN(:uuids)")
    void deleteAllByProjectIds(List<UUID> uuids);
}