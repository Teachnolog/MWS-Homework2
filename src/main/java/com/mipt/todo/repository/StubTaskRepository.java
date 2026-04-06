package com.mipt.todo.repository;

import com.mipt.todo.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class StubTaskRepository implements TaskRepository {

  private final List<Task> tasks = new ArrayList<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  public StubTaskRepository() {
    tasks.add(new Task(idCounter.getAndIncrement(), "Buy groceries", "Milk, eggs, bread", false));
    tasks.add(new Task(idCounter.getAndIncrement(), "Read a book", "Finish the Spring Boot book", true));
    tasks.add(new Task(idCounter.getAndIncrement(), "Exercise", "30 minutes of jogging", false));
  }

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(tasks);
  }

  @Override
  public Optional<Task> findById(Long id) {
    return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
  }

  @Override
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

  @Override
  public void deleteById(Long id) {
    tasks.removeIf(t -> t.getId().equals(id));
  }
}