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
            @RequestParam Long userId)
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
            @RequestHeader(value = "X-Roles", required = false) String roles)
    {
        if (roles == null || !roles.contains("ROLE_USER"))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(
                projectService.getProject(id), HttpStatus.OK
        );
    }

    @PostMapping("/createProject")
    public ResponseEntity<ProjectDto> createProject(@ModelAttribute ProjectInputDto newProject)
    {
        return new ResponseEntity<>(
                projectService.createProject(newProject),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateProject(@PathVariable UUID id,
                                        @ModelAttribute ProjectUpdateDto updatedDto)
    {
        return new ResponseEntity<>(
                projectService.updateProject(id, updatedDto),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id)
    {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}