package com.mipt.todo.controller;

import com.mipt.todo.dto.TaskCreateDto;
import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.model.Priority;
import com.mipt.todo.dto.TaskUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.enabled=false")
class TaskControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  private Long createdTaskId;

  @BeforeEach
  void setUp() {
    TaskCreateDto task = new TaskCreateDto();
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setPriority(Priority.MEDIUM);

    ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity("/api/tasks", task, TaskResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertNotNull(response.getBody());
    createdTaskId = response.getBody().getId();
  }

  @AfterEach
  void tearDown() {
    restTemplate.delete("/api/tasks/" + createdTaskId);
  }

  @Test
  void getAllTasks_positive_returns200AndList() {
    ResponseEntity<TaskResponseDto[]> response = restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isGreaterThan(0);
  }

  @Test
  void getAllTasks_alwaysReturns200() {
    ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getStatistics_positive_returns200AndData() {
    ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/statistics", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("primaryRepositoryTaskCount");
    assertThat(response.getBody()).contains("stubRepositoryTaskCount");
  }

  @Test
  void getAllTasks_negative_unsupportedAccept_returns406() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(java.util.List.of(MediaType.APPLICATION_XML));

    ResponseEntity<String> response = restTemplate.exchange(
        "/api/tasks", HttpMethod.GET, new HttpEntity<>(headers), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
  }

  @Test
  void getTaskById_positive_found() {
    ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity("/api/tasks/" + createdTaskId, TaskResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(createdTaskId);
  }

  @Test
  void getTaskById_negative_notFound() {
    ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity("/api/tasks/999999", TaskResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void createTask_positive_returns201() {
    TaskCreateDto task = new TaskCreateDto();
    task.setTitle("New Task");
    task.setDescription("New Description");
    task.setPriority(Priority.MEDIUM);

    ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity("/api/tasks", task, TaskResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("New Task");
  }

  @Test
  void createTask_negative_nullTitle_returns400() {
    TaskCreateDto task = new TaskCreateDto();

    ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity("/api/tasks", task, TaskResponseDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void updateTask_positive_returns200() {
    TaskUpdateDto update = new TaskUpdateDto();
    update.setTitle("Updated Title");
    update.setDescription("Updated Description");
    update.setCompleted(true);

    HttpEntity<TaskUpdateDto> request = new HttpEntity<>(update);
    ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
      "/api/tasks/" + createdTaskId, HttpMethod.PUT, request, TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
    assertThat(response.getBody().isCompleted()).isTrue();
  }

  @Test
  void updateTask_negative_notFound() {
    TaskUpdateDto update = new TaskUpdateDto();
    update.setTitle("Ghost Task");

    HttpEntity<TaskUpdateDto> request = new HttpEntity<>(update);
    ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
      "/api/tasks/999999", HttpMethod.PUT, request, TaskResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteTask_positive_returns204() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tasks/" + createdTaskId, HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void deleteTask_negative_notFound() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tasks/999999", HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}