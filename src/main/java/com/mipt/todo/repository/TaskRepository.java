package com.mipt.todo.repository;

import com.mipt.todo.model.Priority;
import com.mipt.todo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

  @Query(value = "SELECT * FROM tasks WHERE due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 7", nativeQuery = true)
  List<Task> findDueInNext7Days();

  @Query("select distinct t from Task t left join fetch t.attachments")
  List<Task> findAllWithAttachments();
}