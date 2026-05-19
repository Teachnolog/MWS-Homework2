package com.mipt.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.todo.dto.TaskCreateDto;
import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.dto.TaskUpdateDto;
import com.mipt.todo.mapper.TaskMapper;
import com.mipt.todo.model.Priority;
import com.mipt.todo.model.Task;
import com.mipt.todo.service.TaskService;
import com.mipt.todo.service.TaskStatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TaskService taskService;

  @MockBean
  private TaskStatisticsService statisticsService;

  @MockBean
  private TaskMapper taskMapper;

  @Test
  void createTask_returns201() throws Exception {
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Test");
    createDto.setDescription("Desc");
    createDto.setPriority(Priority.MEDIUM);
    createDto.setDueDate(LocalDate.now().plusDays(5));

    Task entity = new Task();
    Task saved = new Task();
    saved.setId(10L);
    TaskResponseDto resp = new TaskResponseDto();
    resp.setId(10L);
    resp.setTitle("Test");

    when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(entity);
    when(taskService.createTask(any(Task.class))).thenReturn(saved);
    when(taskMapper.toResponseDto(saved)).thenReturn(resp);

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10));
  }

  @Test
  void createTask_withEmptyTitle_returns400() throws Exception {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("");

    mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void getAllTasks_returns200() throws Exception {
    Task t1 = new Task();
    t1.setId(1L);
    Task t2 = new Task();
    t2.setId(2L);

    TaskResponseDto r1 = new TaskResponseDto();
    r1.setId(1L);
    TaskResponseDto r2 = new TaskResponseDto();
    r2.setId(2L);

    when(taskService.getAllTasks()).thenReturn(List.of(t1, t2));
    when(taskMapper.toResponseDto(t1)).thenReturn(r1);
    when(taskMapper.toResponseDto(t2)).thenReturn(r2);

    mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void getTaskById_returns200() throws Exception {
    Task task = new Task();
    task.setId(5L);
    TaskResponseDto resp = new TaskResponseDto();
    resp.setId(5L);

    when(taskService.getTaskById(5L)).thenReturn(Optional.of(task));
    when(taskMapper.toResponseDto(task)).thenReturn(resp);

    mockMvc.perform(get("/api/tasks/5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(5));
  }

  @Test
  void getTaskById_notFound_returns404() throws Exception {
    when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/tasks/99"))
            .andExpect(status().isNotFound());
  }

  @Test
  void updateTask_returns200() throws Exception {
    TaskUpdateDto updateDto = new TaskUpdateDto();
    updateDto.setTitle("Updated");

    Task existing = new Task();
    Task updated = new Task();
    updated.setId(1L);
    TaskResponseDto resp = new TaskResponseDto();
    resp.setId(1L);

    when(taskService.getTaskById(1L)).thenReturn(Optional.of(existing));
    when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(Optional.of(updated));
    when(taskMapper.toResponseDto(updated)).thenReturn(resp);

    mockMvc.perform(put("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void updateTask_notFound_returns404() throws Exception {
    TaskUpdateDto dto = new TaskUpdateDto();
    dto.setTitle("Any");

    when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/api/tasks/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound());
  }

  @Test
  void deleteTask_returns204() throws Exception {
    when(taskService.getTaskById(1L)).thenReturn(Optional.of(new Task()));
    when(taskService.deleteTask(1L)).thenReturn(true);

    mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());
  }

  @Test
  void deleteTask_notFound_returns404() throws Exception {
    when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNotFound());
  }

  @Test
  void getTasks_withUnsupportedAccept_returns406() throws Exception {
    mockMvc.perform(get("/api/tasks")
                    .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
  }
}
