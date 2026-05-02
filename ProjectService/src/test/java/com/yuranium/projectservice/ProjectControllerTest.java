package com.yuranium.projectservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yuranium.projectservice.controller.ProjectController;
import com.yuranium.projectservice.dto.ProjectDto;
import com.yuranium.projectservice.dto.ProjectInputDto;
import com.yuranium.projectservice.dto.ProjectUpdateDto;
import com.yuranium.projectservice.service.ProjectService;
import com.yuranium.projectservice.util.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(projectController)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(jacksonConverter)
                .build();
    }

    @Test
    void getAllProjects_DefaultPaging() throws Exception
    {
        UUID id = UUID.randomUUID();
        ProjectDto dto = new ProjectDto(
                id,
                "Project1",
                "Desc",
                LocalDate.now(),
                LocalDate.now(),
                List.of()
        );
        given(projectService.getAll(PageRequest.of(0, 15,
                Sort.by("dateAdded")), 42L))
                .willReturn(List.of(dto));

        mockMvc.perform(get("/projects/allProjects")
                        .param("userId", "42"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    void getAllProjects_CustomPaging() throws Exception
    {
        UUID id = UUID.randomUUID();
        ProjectDto dto = new ProjectDto(
                id,
                "P2",
                "D2",
                LocalDate.now(),
                LocalDate.now(),
                List.of()
        );
        given(projectService.getAll(PageRequest.of(2, 5,
                Sort.by("dateAdded")), 7L))
                .willReturn(List.of(dto));

        mockMvc.perform(get("/projects/allProjects")
                        .param("pageNumber", "2")
                        .param("size", "5")
                        .param("userId", "7"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    void getProject_Success() throws Exception
    {
        UUID id = UUID.randomUUID();
        ProjectDto dto = new ProjectDto(
                id,
                "Proj",
                "Desc",
                LocalDate.now(),
                LocalDate.now(),
                List.of()
        );
        given(projectService.getProject(id)).willReturn(dto);

        mockMvc.perform(get("/projects/" + id)
                        .header("X-Roles", "ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getProject_Forbidden_NoRole() throws Exception
    {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/projects/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProject_Forbidden_InvalidRole() throws Exception
    {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/projects/" + id)
                        .header("X-Roles", "ROLE_ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProject() throws Exception
    {
        ProjectInputDto input = new ProjectInputDto(
                "New",
                "Desc",
                List.of(),
                99L
        );
        ProjectDto created = new ProjectDto(
                UUID.randomUUID(),
                "New",
                "Desc",
                LocalDate.now(),
                LocalDate.now(),
                List.of()
        );
        given(projectService.createProject(any(ProjectInputDto.class))).willReturn(created);

        mockMvc.perform(multipart("/projects/createProject")
                        .characterEncoding("UTF-8")
                        .param("name", input.name())
                        .param("description", input.description())
                        .param("userId", input.userId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(created)));
    }

    @Test
    void updateProject() throws Exception
    {
        UUID id = UUID.randomUUID();
        ProjectUpdateDto update = new ProjectUpdateDto(
                "Upd",
                "DescU",
                List.of()
        );
        ProjectDto updated = new ProjectDto(
                id,
                "Upd",
                "DescU",
                LocalDate.now(),
                LocalDate.now(),
                List.of()
        );
        given(projectService.updateProject(eq(id), any(ProjectUpdateDto.class)))
                .willReturn(updated);

        mockMvc.perform(multipart("/projects/update/" + id)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .characterEncoding("UTF-8")
                        .param("name", update.name())
                        .param("description", update.description())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    @Test
    void deleteProject() throws Exception
    {
        UUID id = UUID.randomUUID();
        doNothing().when(projectService).deleteProject(id);

        mockMvc.perform(delete("/projects/delete/" + id))
                .andExpect(status().isNoContent());
    }
}