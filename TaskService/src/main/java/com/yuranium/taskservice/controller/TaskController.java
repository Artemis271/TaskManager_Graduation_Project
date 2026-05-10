package com.yuranium.taskservice.controller;

import com.yuranium.taskservice.dto.TaskChartDto;
import com.yuranium.taskservice.dto.TaskDto;
import com.yuranium.taskservice.dto.TaskInputDto;
import com.yuranium.taskservice.dto.TaskUpdateDto;
import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;
import com.yuranium.taskservice.sevice.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController
{
    private final TaskService taskService;

    @GetMapping("/allTasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(@RequestParam UUID projectId)
    {
        return new ResponseEntity<>(
                taskService.getAll(projectId), HttpStatus.OK
        );
    }

    @GetMapping("/allTasks-ProjectIds")
    public ResponseEntity<List<TaskChartDto>> getAllTaskByIds(
            @RequestParam List<UUID> uuids)
    {
        return new ResponseEntity<>(
                taskService.getAllByProjectIds(uuids),
                HttpStatus.OK
        );
    }

    @GetMapping("/allTasksByName")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "15") int size)
    {
        return new ResponseEntity<>(
                taskService.getAllByName(name,
                        PageRequest.of(pageNumber, size)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/allTasksByImportance")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam TaskImportance importance,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "15") int size)
    {
        return new ResponseEntity<>(
                taskService.getAllByTaskImportance(importance,
                        PageRequest.of(pageNumber, size)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/allTasksByStatus")
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam TaskStatus status,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "15") int size)
    {
        return new ResponseEntity<>(
                taskService.getAllByTaskStatus(status,
                        PageRequest.of(pageNumber, size)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                taskService.getTask(id, userId), HttpStatus.OK
        );
    }

    @PostMapping("/createTask")
    public ResponseEntity<?> createTask(
            @ModelAttribute TaskInputDto newTask,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                taskService.createTask(newTask, userId),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable UUID id,
            @RequestBody TaskUpdateDto updatedDto,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                taskService.updateTask(id, updatedDto, userId),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long userId)
    {
        taskService.deleteTask(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/allTaskImportance")
    public ResponseEntity<List<TaskImportance>> getAllTaskImportance()
    {
        return new ResponseEntity<>(
                Arrays.asList(TaskImportance.values()),
                HttpStatus.OK
        );
    }

    @GetMapping("/allTaskStatus")
    public ResponseEntity<List<TaskStatus>> getAllTaskStatus()
    {
        return new ResponseEntity<>(
                Arrays.asList(TaskStatus.values()),
                HttpStatus.OK
        );
    }
}
