package com.mipt.todo.service;

import com.mipt.todo.config.PrototypeScopedBean;
import com.mipt.todo.model.Task;
import com.mipt.todo.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис, который инкапсулирует бизнес-логику работы с задачами и кэшем
 */
@Service
public class TaskService {

  private static final Logger log = LoggerFactory.getLogger(TaskService.class);

  private final TaskRepository taskRepository;
  private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;

  private final Map<String, Task> taskCache = new ConcurrentHashMap<>();

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  public TaskService(TaskRepository taskRepository,
      ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
    this.taskRepository = taskRepository;
    this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
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

  public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }

  public Task createTask(Task task) {
    if (task.getId() == null) {
      task.setId(prototypeScopedBeanProvider.getObject().generateTaskId());
    }
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