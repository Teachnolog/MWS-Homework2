package com.mipt.todo.repository;

import com.mipt.todo.model.Priority;
import com.mipt.todo.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.enabled=false")
public class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void saveAndFind() {
    Task t = new Task();
    t.setTitle("Test");
    t.setDescription("desc");
    t.setPriority(Priority.MEDIUM);
    t.setDueDate(LocalDate.now().plusDays(3));
    t.setCreatedAt(LocalDateTime.now());
    t.setCompleted(false);

    Task saved = taskRepository.save(t);
    assertThat(saved.getId()).isNotNull();

    List<Task> due = taskRepository.findDueInNext7Days();
    assertThat(due).isNotEmpty();
  }
}

