package com.yuranium.projectservice.controller;

import com.yuranium.projectservice.dto.ProjectDto;
import com.yuranium.projectservice.dto.ProjectInputDto;
import com.yuranium.projectservice.dto.ProjectUpdateDto;
import com.yuranium.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController
{
    private final ProjectService projectService;

    @GetMapping("/allProjects")
    public ResponseEntity<List<ProjectDto>> getAllProjects(
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "15") int size,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                projectService.getAll(PageRequest.of(
                        pageNumber, size, Sort.by("dateAdded")), userId),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                projectService.getProject(id, userId), HttpStatus.OK
        );
    }

    @PostMapping("/createProject")
    public ResponseEntity<ProjectDto> createProject(
            @ModelAttribute ProjectInputDto newProject,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                projectService.createProject(newProject, userId),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable UUID id,
            @ModelAttribute ProjectUpdateDto updatedDto,
            @RequestHeader("X-User-Id") Long userId)
    {
        return new ResponseEntity<>(
                projectService.updateProject(id, updatedDto, userId),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long userId)
    {
        projectService.deleteProject(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
