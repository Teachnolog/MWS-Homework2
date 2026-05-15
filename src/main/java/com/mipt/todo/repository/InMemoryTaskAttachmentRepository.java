package com.mipt.todo.repository;

import com.mipt.todo.model.TaskAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория для TaskAttachment.
 */
public class InMemoryTaskAttachmentRepository {

  private final ConcurrentHashMap<Long, TaskAttachment> store = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  public TaskAttachment save(TaskAttachment attachment) {
    if (attachment.getId() == null) {
      attachment.setId(idCounter.getAndIncrement());
    }
    store.put(attachment.getId(), attachment);
    return attachment;
  }

  public Optional<TaskAttachment> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<TaskAttachment> findByTaskId(Long taskId) {
    return new ArrayList<>(store.values().stream()
        .filter(a -> a.getTask() != null && a.getTask().getId().equals(taskId))
        .toList());
  }

  public void deleteById(Long id) {
    store.remove(id);
  }

  public boolean existsById(Long id) {
    return store.containsKey(id);
  }
}

