package com.mipt.todo.controller;

import com.mipt.todo.config.RequestScopedBean;
import com.mipt.todo.model.Task;
import com.mipt.todo.service.TaskStatisticsService;
import com.mipt.todo.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST-контроллер для CRUD-операций над задачами.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private static final Logger log = LoggerFactory.getLogger(TaskController.class);

  private final TaskService taskService;
  private final TaskStatisticsService taskStatisticsService;
  private final RequestScopedBean requestScopedBean;

  public TaskController(TaskService taskService,
      TaskStatisticsService taskStatisticsService,
      RequestScopedBean requestScopedBean) {
    this.taskService = taskService;
    this.taskStatisticsService = taskStatisticsService;
    this.requestScopedBean = requestScopedBean;
  }

  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    logRequestContext("getAllTasks");
    return ResponseEntity.ok(taskService.getAllTasks());
  }

  @GetMapping("/statistics")
  public ResponseEntity<Map<String, Object>> getStatistics() {
    logRequestContext("getStatistics");
    return ResponseEntity.ok(taskStatisticsService.getStatistics());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    logRequestContext("getTaskById");
    Optional<Task> task = taskService.getTaskById(id);
    return task.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody(required = false) Task task) {
    logRequestContext("createTask");
    if (task == null || task.getTitle() == null || task.getTitle().isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    Task created = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id,
      @RequestBody Task task) {
    logRequestContext("updateTask");
    Optional<Task> updated = taskService.updateTask(id, task);
    return updated.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    logRequestContext("deleteTask");
    boolean deleted = taskService.deleteTask(id);
    if (!deleted) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }

  private void logRequestContext(String operation) {
    log.debug("{} handled for request {} started at {}", operation,
        requestScopedBean.getRequestId(), requestScopedBean.getStartTime());
  }
}