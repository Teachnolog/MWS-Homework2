package com.mipt.todo.controller;

import com.mipt.todo.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  private Long createdTaskId;

  @BeforeEach
  void setUp() {
    Task task = new Task();
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setCompleted(false);

    ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", task, Task.class);
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
    ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);
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
    ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/" + createdTaskId, Task.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(createdTaskId);
  }

  @Test
  void getTaskById_negative_notFound() {
    ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/999999", Task.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void createTask_positive_returns201() {
    Task task = new Task();
    task.setTitle("New Task");
    task.setDescription("New Description");

    ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", task, Task.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("New Task");
  }

  @Test
  void createTask_negative_nullTitle_returns400() {
    Task task = new Task();

    ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", task, Task.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void updateTask_positive_returns200() {
    Task update = new Task();
    update.setTitle("Updated Title");
    update.setDescription("Updated Description");
    update.setCompleted(true);

    HttpEntity<Task> request = new HttpEntity<>(update);
    ResponseEntity<Task> response = restTemplate.exchange(
        "/api/tasks/" + createdTaskId, HttpMethod.PUT, request, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
    assertThat(response.getBody().isCompleted()).isTrue();
  }

  @Test
  void updateTask_negative_notFound() {
    Task update = new Task();
    update.setTitle("Ghost Task");

    HttpEntity<Task> request = new HttpEntity<>(update);
    ResponseEntity<Task> response = restTemplate.exchange(
        "/api/tasks/999999", HttpMethod.PUT, request, Task.class);

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