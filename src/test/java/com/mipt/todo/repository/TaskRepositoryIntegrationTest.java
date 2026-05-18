package com.mipt.todo.repository;

import com.mipt.todo.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.testcontainers.DockerClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryIntegrationTest {

  @BeforeAll
  static void checkDocker() {
    Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker not available, skipping integration test");
  }

  @Container
  public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private TaskRepository taskRepository;

  @Test
  void findDueInNext7Days_returnsMatchingTasks() {
    Task t1 = new Task();
    t1.setTitle("A");
    t1.setDueDate(LocalDate.now().plusDays(3));

    Task t2 = new Task();
    t2.setTitle("B");
    t2.setDueDate(LocalDate.now().plusDays(10));

    taskRepository.saveAll(List.of(t1, t2));

    List<Task> due = taskRepository.findDueInNext7Days();
    assertThat(due).extracting(Task::getTitle).contains("A").doesNotContain("B");
  }
}
