package com.yuranium.projectservice.service;

import com.yuranium.projectservice.dto.ProjectDto;
import com.yuranium.projectservice.dto.ProjectInputDto;
import com.yuranium.projectservice.dto.ProjectUpdateDto;
import com.yuranium.projectservice.entity.ProjectEntity;
import com.yuranium.projectservice.mapper.ProjectMapper;
import com.yuranium.projectservice.repository.ProjectRepository;
import com.yuranium.projectservice.util.exception.ProjectEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService
{
    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final AvatarService avatarService;

    private final KafkaProducer kafkaProducer;

    @Transactional(readOnly = true)
    public List<ProjectDto> getAll(Pageable pageable, Long userId)
    {
        return projectMapper.toDto(
                projectRepository
                        .findAllByUserId(pageable, userId)
                        .getContent()
        );
    }

    @Transactional(readOnly = true)
    public ProjectDto getProject(UUID id)
    {
        return projectMapper.toDto(
                projectRepository.findById(id)
                        .orElseThrow(() -> new ProjectEntityNotFoundException(
                                String.format("The project with id=%s was not found!", id))
        ));
    }

    @Transactional
    public ProjectDto createProject(ProjectInputDto newProject)
    {
        ProjectEntity project = projectMapper.toEntity(newProject);
        project.setAvatars(avatarService.multipartToEntity(newProject.avatars()));
        avatarService.saveAll(project.getAvatars());
        return projectMapper.toDto(
                projectRepository.save(project)
        );
    }

    @Transactional
    public ProjectDto updateProject(UUID id, ProjectUpdateDto updatedProject) // ? todo изменить логику связывания новых аватарок
    {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(
                        () -> new ProjectEntityNotFoundException(
                                String.format("The project with id=%s does not exist", id))
                );
        project.setName(updatedProject.name());
        project.setDescription(updatedProject.description());
        if (updatedProject.avatars() != null)
            project.setAvatars(avatarService.multipartToEntity(updatedProject.avatars()));
        avatarService.saveAll(project.getAvatars());
        return projectMapper.toDto(
                        projectRepository.save(project)
        );
    }

    @Transactional
    public void deleteProject(UUID id)
    {
        if (projectRepository.findById(id).isPresent())
        {
            projectRepository.deleteById(id);
            kafkaProducer.sendDeleteProjectEvent(id);
        }
        else throw new ProjectEntityNotFoundException(
                String.format(
                        "The project with id=%s cannot be removed because it does not exist",
                        id
                )
        );
    }

    @Transactional
    public void deleteAllProject(Long userId)
    {
        projectRepository.deleteAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<ProjectEntity> getAllByUserId(Long id)
    {
        return projectRepository.findAllByUserId(id);
    }
}