package com.mipt.todo.repository;

import com.mipt.todo.model.TaskAttachment;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для TaskAttachment.
 */
public interface TaskAttachmentRepository {

  TaskAttachment save(TaskAttachment attachment);

  Optional<TaskAttachment> findById(Long id);

  List<TaskAttachment> findByTaskId(Long taskId);

  void deleteById(Long id);

  boolean existsById(Long id);
}

