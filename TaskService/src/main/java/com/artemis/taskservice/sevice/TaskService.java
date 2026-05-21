package com.artemis.taskservice.sevice;

import com.artemis.taskservice.dto.TaskChartDto;
import com.artemis.taskservice.dto.TaskDto;
import com.artemis.taskservice.dto.TaskInputDto;
import com.artemis.taskservice.dto.TaskUpdateDto;
import com.artemis.taskservice.entity.TaskEntity;
import com.artemis.taskservice.enums.TaskImportance;
import com.artemis.taskservice.enums.TaskStatus;
import com.artemis.taskservice.mapper.TaskMapper;
import com.artemis.taskservice.repository.TaskRepository;
import com.artemis.taskservice.sevice.kafka.KafkaProducer;
import com.artemis.taskservice.util.exception.AccessDeniedException;
import com.artemis.taskservice.util.exception.TaskEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService
{
    private final TaskRepository taskRepository;

    private final TaskImageService imageService;

    private final TaskMapper taskMapper;

    private final KafkaProducer kafkaProducer;

    @Transactional(readOnly = true)
    public List<TaskDto> getAll(UUID projectId)
    {
        List<TaskEntity> taskEntities = taskRepository.findAllByProjectId(projectId,
                PageRequest.of(0, 15, Sort.by("dateAdded")));
        taskEntities.forEach(task -> task.getImages().size());
        return taskMapper.toDto(taskEntities);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllByName(String name, Pageable pageable)
    {
        return taskMapper.toDto(
                taskRepository.findByName(name, pageable)
                        .stream()
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllByTaskImportance(TaskImportance importance, Pageable pageable)
    {
        return taskMapper.toDto(
                taskRepository.findByTaskImportance(importance, pageable)
                        .stream()
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllByTaskStatus(TaskStatus status, Pageable pageable)
    {
        return taskMapper.toDto(
                taskRepository.findByTaskStatus(status, pageable)
                        .stream()
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public TaskDto getTask(UUID id, Long userId)
    {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskEntityNotFoundException(
                        String.format("The task with id=%s was not found!", id)));
        if (!task.getOwnerId().equals(userId))
            throw new AccessDeniedException("Access denied to task with id=" + id);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDto createTask(TaskInputDto newTask, Long userId)
    {
        TaskEntity task = taskMapper.toEntity(newTask);
        task.setOwnerId(userId);
        task.setImages(imageService.multipartToEntity(newTask.images()));
        imageService.saveAll(task.getImages());
        TaskEntity saved = taskRepository.save(task);
        kafkaProducer.sendTaskCreatedEvent(saved);
        return taskMapper.toDto(saved);
    }

    @Transactional
    public TaskDto updateTask(UUID id, TaskUpdateDto updatedTask, Long userId)
    {
        TaskEntity existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskEntityNotFoundException(
                        String.format("The task with id=%s does not exist", id)
                ));
        if (!existing.getOwnerId().equals(userId))
            throw new AccessDeniedException("Access denied to task with id=" + id);

        String oldStatus = existing.getTaskStatus() != null
                ? existing.getTaskStatus().name() : null;

        TaskEntity updated = taskMapper.toEntity(updatedTask);
        updated.setId(id);
        updated.setProjectId(existing.getProjectId());
        updated.setAssigneeId(existing.getAssigneeId());
        updated.setOwnerId(existing.getOwnerId());
        TaskEntity saved = taskRepository.save(updated);

        if (updatedTask.taskStatus() != null
                && !updatedTask.taskStatus().name().equals(oldStatus))
            kafkaProducer.sendTaskStatusChangedEvent(saved, oldStatus);

        return taskMapper.toDto(saved);
    }

    @Transactional
    public void deleteTask(UUID id, Long userId)
    {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskEntityNotFoundException(
                        String.format("The task with id=%s cannot be removed because it does not exist", id)));
        if (!task.getOwnerId().equals(userId))
            throw new AccessDeniedException("Access denied to task with id=" + id);
        taskRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllTask(UUID id)
    {
        taskRepository.deleteAllByProjectId(id);
    }

    @Transactional
    public void deleteAllTask(List<UUID> uuids)
    {
        taskRepository.deleteAllByProjectIds(uuids);
    }

    @Transactional(readOnly = true)
    public List<TaskChartDto> getAllByProjectIds(List<UUID> uuids)
    {
        return taskMapper.toChartDto(
                taskRepository.findByProjectIds(uuids)
        );
    }
}