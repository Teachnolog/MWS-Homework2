package com.mipt.todo.external;

import com.mipt.todo.dto.ExternalTaskCreateDto;
import com.mipt.todo.dto.ExternalTaskResponseDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/v1")
@Profile({"dev", "test"})
public class ExternalApiController {

  private final AtomicLong idGenerator = new AtomicLong(1);
  private final Map<Long, ExternalTaskResponseDto> tasks = new ConcurrentHashMap<>();

  @PostMapping("/tasks")
  public ResponseEntity<Void> createTask(@Valid @RequestBody ExternalTaskCreateDto request) {
    long id = idGenerator.getAndIncrement();
    ExternalTaskResponseDto dto = new ExternalTaskResponseDto();
    dto.setId(id);
    dto.setTitle(request.getTitle());
    dto.setDescription(request.getDescription());
    dto.setCompleted(false);
    tasks.put(id, dto);

    URI location = URI.create("/external/v1/tasks/" + id);
    return ResponseEntity.created(location).build();
  }

  @GetMapping("/tasks/{id}")
  public ResponseEntity<ExternalTaskResponseDto> getTaskById(@PathVariable Long id) {
    ExternalTaskResponseDto task = tasks.get(id);
    if (task == null) {
      throw taskNotFound(id);
    }
    return ResponseEntity.ok(task);
  }

  @GetMapping("/tasks")
  public ResponseEntity<List<ExternalTaskResponseDto>> getTasks(
      @RequestParam(required = false) Boolean completed,
      @RequestParam(required = false) Integer limit) {
    List<ExternalTaskResponseDto> result = new ArrayList<>(tasks.values());
    result.sort(Comparator.comparing(ExternalTaskResponseDto::getId));

    if (completed != null) {
      result = result.stream().filter(t -> t.isCompleted() == completed).toList();
    }
    if (limit != null && limit > 0 && result.size() > limit) {
      result = result.subList(0, limit);
    }

    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    ExternalTaskResponseDto removed = tasks.remove(id);
    if (removed == null) {
      throw taskNotFound(id);
    }
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/unstable")
  public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
    switch (mode) {
      case "timeout":
        Thread.sleep(3000L);
        return ResponseEntity.ok(Map.of("mode", "timeout", "status", "late response"));
      case "500":
        return ResponseEntity.internalServerError().body(Map.of("error", "simulated internal error"));
      case "429":
        return ResponseEntity.status(429)
            .header(HttpHeaders.RETRY_AFTER, "5")
            .body(Map.of("error", "too many requests"));
      case "html":
        return ResponseEntity.status(502)
            .contentType(MediaType.TEXT_HTML)
            .body("<html><body>Bad gateway</body></html>");
      default:
        return ResponseEntity.badRequest().body(Map.of("error", "unknown mode"));
    }
  }

  private RuntimeException taskNotFound(Long id) {
    ProblemDetail pd = ProblemDetail.forStatus(404);
    pd.setTitle("Task Not Found");
    pd.setDetail("Task with id=" + id + " was not found in external API");
    return new ErrorResponseException(HttpStatus.NOT_FOUND, pd, null);
  }
}


