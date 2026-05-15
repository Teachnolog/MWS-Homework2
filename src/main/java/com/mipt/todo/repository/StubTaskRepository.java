package com.mipt.todo.repository;

import com.mipt.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stub-репозиторий с предзаполненными данными для демонстрации @Bean и @Qualifier.
 */
public class StubTaskRepository {

  private final List<Task> tasks = new ArrayList<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  public StubTaskRepository() {
    Task t1 = new Task();
    t1.setId(idCounter.getAndIncrement());
    t1.setTitle("Buy groceries");
    t1.setDescription("Milk, eggs, bread");
    t1.setCompleted(false);
    tasks.add(t1);

    Task t2 = new Task();
    t2.setId(idCounter.getAndIncrement());
    t2.setTitle("Read a book");
    t2.setDescription("Finish the Spring Boot book");
    t2.setCompleted(true);
    tasks.add(t2);

    Task t3 = new Task();
    t3.setId(idCounter.getAndIncrement());
    t3.setTitle("Exercise");
    t3.setDescription("30 minutes of jogging");
    t3.setCompleted(false);
    tasks.add(t3);
  }

  public List<Task> findAll() {
    return new ArrayList<>(tasks);
  }

  public Optional<Task> findById(Long id) {
    return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
  }

  public Task save(Task task) {
    if (task.getId() == null) {
      task.setId(idCounter.getAndIncrement());
      tasks.add(task);
    } else {
      tasks.removeIf(t -> t.getId().equals(task.getId()));
      tasks.add(task);
    }
    return task;
  }

  public void deleteById(Long id) {
    tasks.removeIf(t -> t.getId().equals(id));
  }
}