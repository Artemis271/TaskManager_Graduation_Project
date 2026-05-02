package com.yuranium.taskservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yuranium.taskservice.controller.TaskController;
import com.yuranium.taskservice.dto.TaskChartDto;
import com.yuranium.taskservice.dto.TaskDto;
import com.yuranium.taskservice.dto.TaskInputDto;
import com.yuranium.taskservice.dto.TaskUpdateDto;
import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;
import com.yuranium.taskservice.sevice.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void getAllTasks_ByProjectId() throws Exception {
        UUID pid = UUID.randomUUID();
        TaskDto dto = new TaskDto(pid, "Task1", "Desc1", TaskImportance.HIGH,
                TaskStatus.PLANING, LocalDate.now(), LocalDate.now(), false, List.of());
        given(taskService.getAll(pid)).willReturn(List.of(dto));

        mockMvc.perform(get("/tasks/allTasks").param("projectId", pid.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    void getAllTaskByIds() throws Exception {
        UUID id1 = UUID.randomUUID();
        List<UUID> uuids = List.of(id1);
        TaskChartDto chart = new TaskChartDto(id1, "Task1", TaskStatus.IN_PROGRESS,
                TaskImportance.LOW, LocalDate.now(), UUID.randomUUID());
        given(taskService.getAllByProjectIds(uuids)).willReturn(List.of(chart));

        mockMvc.perform(get("/tasks/allTasks-ProjectIds")
                        .param("uuids", id1.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(chart))));
    }

    @Test
    void getAllTasks_ByName() throws Exception {
        String name = "Test";
        TaskDto dto = new TaskDto(UUID.randomUUID(), name, "Desc", TaskImportance.INTERMEDIATE,
                TaskStatus.COMPLETED, LocalDate.now(), LocalDate.now(), true, List.of());
        given(taskService.getAllByName(eq(name), any())).willReturn(List.of(dto));

        mockMvc.perform(get("/tasks/allTasksByName")
                        .param("name", name)
                        .param("pageNumber", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    void getAllTasks_ByImportance() throws Exception {
        TaskImportance imp = TaskImportance.LOW;
        TaskDto dto = new TaskDto(UUID.randomUUID(), "T", "D", imp,
                TaskStatus.EXPIRED, LocalDate.now(), LocalDate.now(), false, List.of());
        given(taskService.getAllByTaskImportance(eq(imp), any())).willReturn(List.of(dto));

        mockMvc.perform(get("/tasks/allTasksByImportance")
                        .param("importance", imp.name())
                        .param("pageNumber", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    void getAllTasks_ByStatus() throws Exception {
        TaskStatus status = TaskStatus.CANCELED;
        TaskDto dto = new TaskDto(UUID.randomUUID(), "X", "Y", TaskImportance.HIGH,
                status, LocalDate.now(), LocalDate.now(), false, List.of());
        given(taskService.getAllByTaskStatus(eq(status), any())).willReturn(List.of(dto));

        mockMvc.perform(get("/tasks/allTasksByStatus")
                        .param("status", status.name())
                        .param("pageNumber", "2")
                        .param("size", "7"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @Test
    void getTask_ById() throws Exception {
        UUID id = UUID.randomUUID();
        TaskDto dto = new TaskDto(id, "Name", "Desc", TaskImportance.HIGH,
                TaskStatus.IN_PROGRESS, LocalDate.now(), LocalDate.now(), false, List.of());
        given(taskService.getTask(id)).willReturn(dto);

        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void createTask() throws Exception {
        UUID pid = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("images", "img.png",
                "image/png", "data".getBytes());
        TaskInputDto input = new TaskInputDto("N", "D", TaskImportance.LOW,
                TaskStatus.PLANING, LocalDate.now(), List.of(file), pid);
        TaskDto created = new TaskDto(UUID.randomUUID(), "N", "D", input.taskImportance(),
                input.taskStatus(), LocalDate.now(), input.dateFinished(), false, List.of());
        given(taskService.createTask(any(TaskInputDto.class))).willReturn(created);

        mockMvc.perform(multipart("/tasks/createTask")
                        .file(file)
                        .param("name", input.name())
                        .param("description", input.description())
                        .param("taskImportance", input.taskImportance().name())
                        .param("taskStatus", input.taskStatus().name())
                        .param("dateFinished", input.dateFinished().toString())
                        .param("projectId", pid.toString())
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(created)));
    }

    @Test
    void updateTask() throws Exception {
        UUID id = UUID.randomUUID();
        TaskUpdateDto update = new TaskUpdateDto("U", "D", TaskImportance.HIGH,
                TaskStatus.COMPLETED, LocalDate.now(), true);
        TaskDto updated = new TaskDto(id, update.name(), update.description(),
                update.taskImportance(), update.taskStatus(), LocalDate.now(),
                update.dateFinished(), update.isFinished(), List.of());
        given(taskService.updateTask(eq(id), any(TaskUpdateDto.class))).willReturn(updated);

        mockMvc.perform(patch("/tasks/update/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
    }

    @Test
    void deleteTask() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/tasks/delete/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllTaskImportance() throws Exception {
        mockMvc.perform(get("/tasks/allTaskImportance"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        Arrays.asList(TaskImportance.values()))));
    }

    @Test
    void getAllTaskStatus() throws Exception {
        mockMvc.perform(get("/tasks/allTaskStatus"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        Arrays.asList(TaskStatus.values()))));
    }
}