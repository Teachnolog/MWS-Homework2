package com.mipt.todo.controller;

import com.mipt.todo.dto.ExternalTaskCreateDto;
import com.mipt.todo.dto.ExternalTaskResponseDto;
import com.mipt.todo.exception.ExternalApiException;
import com.mipt.todo.exception.TaskNotFoundException;
import com.mipt.todo.service.TasksGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TasksGatewayController.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.enabled=false")
@AutoConfigureMockMvc(addFilters = false)
class TasksGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TasksGatewayService tasksGatewayService;

    @Test
    void createTask_shouldReturn201WithLocation() throws Exception {
        ExternalTaskCreateDto createDto = new ExternalTaskCreateDto();
        createDto.setTitle("Test Task");
        createDto.setDescription("Description");

        ExternalTaskResponseDto responseDto = new ExternalTaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Task");
        responseDto.setDescription("Description");
        responseDto.setCompleted(false);

        when(tasksGatewayService.createTask(any(ExternalTaskCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Task\",\"description\":\"Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        ExternalTaskResponseDto responseDto = new ExternalTaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Task");
        responseDto.setCompleted(false);

        when(tasksGatewayService.getTaskById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getTaskById_whenNotFound_shouldReturn404() throws Exception {
        when(tasksGatewayService.getTaskById(anyLong()))
                .thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasks_withValidLimit_shouldReturnList() throws Exception {
        ExternalTaskResponseDto task1 = new ExternalTaskResponseDto();
        task1.setId(1L);
        task1.setTitle("Task1");
        ExternalTaskResponseDto task2 = new ExternalTaskResponseDto();
        task2.setId(2L);
        task2.setTitle("Task2");

        when(tasksGatewayService.getTasks(eq(true), eq(10))).thenReturn(List.of(task1, task2));

        mockMvc.perform(get("/api/v1/tasks?completed=true&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task1"));
    }

    @Test
    void getTasks_withInvalidLimit_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks?limit=0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/tasks?limit=200"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/tasks?limit=-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {
        doNothing().when(tasksGatewayService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new TaskNotFoundException("Task not found"))
                .when(tasksGatewayService).deleteTask(anyLong());

        mockMvc.perform(delete("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasks_whenExternalApiThrows_shouldReturn502() throws Exception {
        when(tasksGatewayService.getTasks(any(), any()))
                .thenThrow(new ExternalApiException("External API failure"));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void createTask_whenExternalRateLimit_shouldReturn429() throws Exception {
        when(tasksGatewayService.createTask(any(ExternalTaskCreateDto.class)))
                .thenThrow(new ExternalApiException("Rate limit exceeded"));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\",\"description\":\"Desc\"}"))
                .andExpect(status().isTooManyRequests());
    }
}