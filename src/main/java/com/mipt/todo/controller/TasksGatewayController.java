package com.mipt.todo.controller;

import com.mipt.todo.dto.ExternalTaskCreateDto;
import com.mipt.todo.dto.ExternalTaskResponseDto;
import com.mipt.todo.service.TasksGatewayService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TasksGatewayController {

  private final TasksGatewayService tasksGatewayService;

  public TasksGatewayController(TasksGatewayService tasksGatewayService) {
    this.tasksGatewayService = tasksGatewayService;
  }

  @PostMapping
  public ResponseEntity<ExternalTaskResponseDto> createTask(
      @Valid @RequestBody ExternalTaskCreateDto request) {
    ExternalTaskResponseDto created = tasksGatewayService.createTask(request);
    URI location = created.getId() == null ? null : URI.create("/api/v1/tasks/" + created.getId());
    if (location != null) {
      return ResponseEntity.created(location).body(created);
    }
    return ResponseEntity.status(201).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExternalTaskResponseDto> getTaskById(@PathVariable Long id) {
    return ResponseEntity.ok(tasksGatewayService.getTaskById(id));
  }

  @GetMapping
  public ResponseEntity<List<ExternalTaskResponseDto>> getTasks(
      @RequestParam(required = false) Boolean completed,
      @RequestParam(required = false) @Min(1) @Max(100) Integer limit) {   // валидация limit
    return ResponseEntity.ok(tasksGatewayService.getTasks(completed, limit));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    tasksGatewayService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
