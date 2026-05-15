package com.mipt.todo.repository;

import com.mipt.todo.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория для TaskAttachment.
 */
@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

  private final ConcurrentHashMap<Long, TaskAttachment> store = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  @Override
  public TaskAttachment save(TaskAttachment attachment) {
    if (attachment.getId() == null) {
      attachment.setId(idCounter.getAndIncrement());
    }
    store.put(attachment.getId(), attachment);
    return attachment;
  }

  @Override
  public Optional<TaskAttachment> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<TaskAttachment> findByTaskId(Long taskId) {
    return new ArrayList<>(store.values().stream()
        .filter(a -> a.getTaskId().equals(taskId))
        .toList());
  }

  @Override
  public void deleteById(Long id) {
    store.remove(id);
  }

  @Override
  public boolean existsById(Long id) {
    return store.containsKey(id);
  }
}

