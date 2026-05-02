package com.yuranium.taskservice.sevice;

import com.yuranium.taskservice.dto.TaskChartDto;
import com.yuranium.taskservice.dto.TaskDto;
import com.yuranium.taskservice.dto.TaskInputDto;
import com.yuranium.taskservice.dto.TaskUpdateDto;
import com.yuranium.taskservice.entity.TaskEntity;
import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;
import com.yuranium.taskservice.mapper.TaskMapper;
import com.yuranium.taskservice.repository.TaskRepository;
import com.yuranium.taskservice.util.exception.TaskEntityNotFoundException;
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
    public TaskDto getTask(UUID id)
    {
        return taskMapper.toDto(
                taskRepository.findById(id)
                        .orElseThrow(() -> new TaskEntityNotFoundException(
                                String.format("The task with id=%s was not found!", id)
                        ))
        );
    }

    @Transactional
    public TaskDto createTask(TaskInputDto newTask)
    {
        TaskEntity task = taskMapper.toEntity(newTask);
        task.setImages(imageService.multipartToEntity(newTask.images()));
        imageService.saveAll(task.getImages());
        return taskMapper.toDto(
                taskRepository.save(task)
        );
    }

    @Transactional
    public TaskDto updateTask(UUID id, TaskUpdateDto updatedTask)
    {
        if (taskRepository.findById(id).isPresent())
            return taskMapper.toDto(taskRepository.save(
                    taskMapper.toEntity(updatedTask)
            ));
        else throw new TaskEntityNotFoundException(
                String.format("The task with id=%s does not exist", id)
        );
    }

    @Transactional
    public void deleteTask(UUID id)
    {
        if (taskRepository.findById(id).isPresent())
            taskRepository.deleteById(id);
        else throw new TaskEntityNotFoundException(
                String.format(
                        "The task with id=%s cannot be removed because it does not exist",
                        id
                )
        );
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