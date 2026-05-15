package com.mipt.todo.service;

import com.mipt.todo.exception.TaskNotFoundException;
import com.mipt.todo.model.Task;
import com.mipt.todo.model.Priority;
import com.mipt.todo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceIntegrationTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void bulkCompleteTasks_shouldRollbackWhenIdNotFound() {
    Task t1 = new Task();
    t1.setTitle("t1");
    t1.setPriority(Priority.MEDIUM);
    t1.setDueDate(LocalDate.now().plusDays(1));

    Task t2 = new Task();
    t2.setTitle("t2");
    t2.setPriority(Priority.LOW);
    t2.setDueDate(LocalDate.now().plusDays(2));

    Task saved1 = taskRepository.save(t1);
    Task saved2 = taskRepository.save(t2);

    List<Long> ids = new ArrayList<>();
    ids.add(saved1.getId());
    ids.add(saved2.getId());
    ids.add(999999L);

    assertThrows(TaskNotFoundException.class, () -> taskService.bulkCompleteTasks(ids));

    Task reloaded1 = taskRepository.findById(saved1.getId()).orElseThrow();
    Task reloaded2 = taskRepository.findById(saved2.getId()).orElseThrow();

    assertThat(reloaded1.isCompleted()).isFalse();
    assertThat(reloaded2.isCompleted()).isFalse();
  }
}
