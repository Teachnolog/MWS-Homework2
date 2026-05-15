package com.mipt.todo.repository;

import com.mipt.todo.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
  List<TaskAttachment> findByTask_Id(Long taskId);
}

