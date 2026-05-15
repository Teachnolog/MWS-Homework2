package com.mipt.todo.service;

import com.mipt.todo.model.Task;
import com.mipt.todo.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.mipt.todo.exception.TaskNotFoundException;

/**
 * Сервис, который инкапсулирует бизнес-логику работы с задачами и кэшем
 */
@Service
public class TaskService {

  private static final Logger log = LoggerFactory.getLogger(TaskService.class);

  private final TaskRepository taskRepository;

  private final Map<String, Task> taskCache = new ConcurrentHashMap<>();

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = TaskNotFoundException.class)
  public void bulkCompleteTasks(List<Long> ids) {
    List<Task> tasks = taskRepository.findAllById(ids);
    if (tasks.size() != ids.size()) {
      throw new TaskNotFoundException("One or more task IDs not found in the provided list");
    }
    for (Task t : tasks) {
      t.setCompleted(true);
    }
    taskRepository.saveAll(tasks);
    tasks.forEach(t -> taskCache.put(String.valueOf(t.getId()), t));
  }

  @PostConstruct
  public void init() {
    log.info("Initialising TaskService for app '{}' v{}", appName, appVersion);
    taskRepository.findAll().forEach(t -> taskCache.put(String.valueOf(t.getId()), t));
    log.info("Cache loaded with {} task(s)", taskCache.size());
  }

  @PreDestroy
  public void destroy() {
    log.info("TaskService shutting down. Cache size: {}", taskCache.size());
  }

  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  public List<Task> getAllTasksWithAttachments() {
    return taskRepository.findAllWithAttachments();
  }

  public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }

  public Task createTask(Task task) {
    Task saved = taskRepository.save(task);
    taskCache.put(String.valueOf(saved.getId()), saved);
    return saved;
  }

  public Optional<Task> updateTask(Long id, Task task) {
    Optional<Task> existing = taskRepository.findById(id);
    if (existing.isEmpty()) {
      return Optional.empty();
    }
    task.setId(id);
    Task updated = taskRepository.save(task);
    taskCache.put(String.valueOf(id), updated);
    return Optional.of(updated);
  }

  public boolean deleteTask(Long id) {
    if (taskRepository.findById(id).isEmpty()) {
      return false;
    }
    taskRepository.deleteById(id);
    taskCache.remove(String.valueOf(id));
    return true;
  }
}