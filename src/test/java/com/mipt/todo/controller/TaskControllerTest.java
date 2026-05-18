package com.mipt.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.todo.dto.TaskCreateDto;
import com.mipt.todo.model.Priority;
import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.mapper.TaskMapper;
import com.mipt.todo.model.Task;
import com.mipt.todo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private TaskService taskService;

  @MockBean
  private com.mipt.todo.service.TaskStatisticsService statisticsService;

  @MockBean
  private TaskMapper taskMapper;

  @Test
  void createTask_returns201_withBody() throws Exception {
    TaskCreateDto createDto = new TaskCreateDto();
    createDto.setTitle("Test");
    createDto.setDescription("Desc");
    createDto.setPriority(Priority.MEDIUM);

    Task entity = new Task(); entity.setTitle("Test"); entity.setDescription("Desc");
    Task saved = new Task(); saved.setId(10L); saved.setTitle("Test"); saved.setDescription("Desc");
    TaskResponseDto resp = new TaskResponseDto(); resp.setId(10L); resp.setTitle("Test");

    when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(entity);
    when(taskService.createTask(any(Task.class))).thenReturn(saved);
    when(taskMapper.toResponseDto(saved)).thenReturn(resp);

    mockMvc.perform(post("/api/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.title").value("Test"));
  }

  @Test
  void getTaskById_returns200_withBody() throws Exception {
    Task t = new Task(); t.setId(5L); t.setTitle("T5");
    TaskResponseDto resp = new TaskResponseDto(); resp.setId(5L); resp.setTitle("T5");

    when(taskService.getTaskById(5L)).thenReturn(java.util.Optional.of(t));
    when(taskMapper.toResponseDto(t)).thenReturn(resp);

    mockMvc.perform(get("/api/tasks/5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(5))
        .andExpect(jsonPath("$.title").value("T5"));
  }
}


