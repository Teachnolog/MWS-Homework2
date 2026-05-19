package com.mipt.todo.service;

import com.mipt.todo.client.ExternalTasksClient;
import com.mipt.todo.dto.ExternalTaskCreateDto;
import com.mipt.todo.dto.ExternalTaskResponseDto;
import com.mipt.todo.exception.ExternalApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TasksGatewayService {

  private final ExternalTasksClient externalTasksClient;

  public TasksGatewayService(ExternalTasksClient externalTasksClient) {
    this.externalTasksClient = externalTasksClient;
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
  public ExternalTaskResponseDto createTask(ExternalTaskCreateDto dto) {
    URI location = externalTasksClient.createTask(dto);
    Long createdId = extractIdFromLocation(location);

    ExternalTaskResponseDto response = new ExternalTaskResponseDto();
    response.setId(createdId);
    response.setTitle(dto.getTitle());
    response.setDescription(dto.getDescription());
    response.setCompleted(false);
    return response;
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskByIdFallback")
  public ExternalTaskResponseDto getTaskById(Long id) {
    return externalTasksClient.getTaskById(id);
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "getTasksFallback")
  public List<ExternalTaskResponseDto> getTasks(Boolean completed, Integer limit) {
    return externalTasksClient.getTasks(completed, limit);
  }

  @RateLimiter(name = "externalApi")
  @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
  public void deleteTask(Long id) {
    externalTasksClient.deleteTask(id);
  }

  public ExternalTaskResponseDto createTaskFallback(ExternalTaskCreateDto dto, Throwable throwable) {
    throw new ExternalApiException("External API unavailable for createTask: " + throwable.getMessage());
  }

  public ExternalTaskResponseDto getTaskByIdFallback(Long id, Throwable throwable) {
    ExternalTaskResponseDto fallback = new ExternalTaskResponseDto();
    fallback.setId(id);
    fallback.setTitle("temporary-unavailable");
    fallback.setDescription("Returning graceful fallback due to: " + throwable.getClass().getSimpleName());
    fallback.setCompleted(false);
    return fallback;
  }

  public List<ExternalTaskResponseDto> getTasksFallback(Boolean completed, Integer limit, Throwable throwable) {
    return Collections.emptyList();
  }

  public void deleteTaskFallback(Long id, Throwable throwable) {
    throw new ExternalApiException("Delete fallback: external API is unavailable");
  }

  private Long extractIdFromLocation(URI location) {
    if (location == null || location.getPath() == null) {
      return null;
    }
    String path = location.getPath();
    int lastSlash = path.lastIndexOf('/');
    if (lastSlash < 0 || lastSlash == path.length() - 1) {
      return null;
    }
    try {
      return Long.valueOf(path.substring(lastSlash + 1));
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
