package com.mipt.todo.controller;
import com.mipt.todo.exception.TaskNotFoundException;
import com.mipt.todo.dto.OnCreate;
import com.mipt.todo.dto.OnUpdate;
import com.mipt.todo.dto.TaskCreateDto;
import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.dto.TaskUpdateDto;
import com.mipt.todo.mapper.TaskMapper;
import com.mipt.todo.model.Task;
import com.mipt.todo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.List;

/**
 * REST-контроллер для CRUD-операций над задачами с использованием DTO.
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
@Tag(name = "Tasks", description = "API для управления задачами")
public class TaskController {

  @Value("${app.api-version:2.0.0}")
  private String apiVersion;

  private final TaskService taskService;
  private final com.mipt.todo.service.TaskStatisticsService statisticsService;
  private final TaskMapper taskMapper;

  public TaskController(TaskService taskService,
      com.mipt.todo.service.TaskStatisticsService statisticsService,
      TaskMapper taskMapper) {
    this.taskService = taskService;
    this.statisticsService = statisticsService;
    this.taskMapper = taskMapper;
  }

  @GetMapping
  @Operation(summary = "Получить все задачи")
  public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
    List<Task> tasks = taskService.getAllTasks();
    List<TaskResponseDto> dtos = tasks.stream().map(taskMapper::toResponseDto).toList();
    return ResponseEntity.ok()
        .header("X-Total-Count", String.valueOf(dtos.size()))
        .header("X-API-Version", apiVersion)
        .body(dtos);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить задачу по ID")
  public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
    Task task = taskService.getTaskById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(taskMapper.toResponseDto(task));
  }

  @PostMapping
  @Operation(summary = "Создать новую задачу")
  public ResponseEntity<TaskResponseDto> createTask(
      @Validated(OnCreate.class) @Valid @RequestBody TaskCreateDto createDto) {
    Task task = taskMapper.toEntity(createDto);
    if (task.getCreatedAt() == null) task.setCreatedAt(java.time.LocalDateTime.now());
    Task saved = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("X-API-Version", apiVersion)
        .body(taskMapper.toResponseDto(saved));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить задачу")
  public ResponseEntity<TaskResponseDto> updateTask(
      @PathVariable Long id,
      @Validated(OnUpdate.class) @Valid @RequestBody TaskUpdateDto updateDto) {
    Task existing = taskService.getTaskById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

    taskMapper.updateEntity(updateDto, existing);

    if (updateDto.getDueDate() != null && existing.getCreatedAt() != null) {
      if (updateDto.getDueDate().isBefore(existing.getCreatedAt().toLocalDate())) {
        throw new IllegalArgumentException("Due date cannot be before creation date");
      }
    }

    java.util.Optional<Task> updated = taskService.updateTask(id, existing);
    if (updated.isEmpty()) {
      throw new TaskNotFoundException("Failed to update task with id: " + id);
    }
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(taskMapper.toResponseDto(updated.get()));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить задачу")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    boolean deleted = taskService.deleteTask(id);
    if (!deleted) {
      throw new TaskNotFoundException("Task not found with id: " + id);
    }
    return ResponseEntity.noContent()
        .header("X-API-Version", apiVersion)
        .build();
  }

  @GetMapping("/statistics")
  @Operation(summary = "Получить статистику задач")
  public ResponseEntity<Map<String, Object>> getStatistics() {
    return ResponseEntity.ok()
        .header("X-API-Version", apiVersion)
        .body(statisticsService.getStatistics());
  }
}

