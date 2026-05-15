package com.mipt.todo.controller;

import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.mapper.TaskMapper;
import com.mipt.todo.service.FavoritesService;
import com.mipt.todo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * Контроллер для управления избранными задачами через сессию.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

  private static final String FAVORITES_KEY = "favoriteTaskIds";

  private final TaskService taskService;
  private final TaskMapper taskMapper;

  public FavoritesController(TaskService taskService, FavoritesService favoritesService,
      TaskMapper taskMapper) {
    this.taskService = taskService;
    this.taskMapper = taskMapper;
  }

  @PostMapping("/{taskId}")
  public ResponseEntity<Void> addToFavorites(@PathVariable Long taskId, HttpSession session) {
    @SuppressWarnings("unchecked")
    Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_KEY);
    if (favorites == null) {
      favorites = new java.util.HashSet<>();
    }
    favorites.add(taskId);
    session.setAttribute(FAVORITES_KEY, favorites);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
    @SuppressWarnings("unchecked")
    Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_KEY);
    if (favorites != null) {
      favorites.remove(taskId);
      session.setAttribute(FAVORITES_KEY, favorites);
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
    @SuppressWarnings("unchecked")
    Set<Long> favorites = (Set<Long>) session.getAttribute(FAVORITES_KEY);
    if (favorites == null || favorites.isEmpty()) {
      return ResponseEntity.ok(List.of());
    }

    List<TaskResponseDto> favoriteTasks = favorites.stream()
        .map(id -> taskService.getTaskById(id)
            .map(taskMapper::toResponseDto)
            .orElse(null))
        .filter(dto -> dto != null)
        .toList();

    return ResponseEntity.ok(favoriteTasks);
  }
}

