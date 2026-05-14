package com.mipt.todo.repository;

import com.mipt.todo.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Контракт хранилища задач с CRUD-операциями.
 */
public interface TaskRepository {

  List<Task> findAll();

  Optional<Task> findById(Long id);

  Task save(Task task);

  void deleteById(Long id);
}