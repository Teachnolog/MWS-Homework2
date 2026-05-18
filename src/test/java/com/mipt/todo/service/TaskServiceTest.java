package com.mipt.todo.service;

import com.mipt.todo.exception.TaskNotFoundException;
import com.mipt.todo.model.Task;
import com.mipt.todo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@org.springframework.test.context.ActiveProfiles("test")
class TaskServiceTest {

  @Autowired
  private TaskService taskService;

  @MockBean
  private TaskRepository taskRepository;

  @Test
  void bulkCompleteTasks_existingTasks_marksCompletedAndSaves() {
    Task t1 = new Task(); t1.setId(1L); t1.setCompleted(false);
    Task t2 = new Task(); t2.setId(2L); t2.setCompleted(false);

    when(taskRepository.findAllById(List.of(1L,2L))).thenReturn(List.of(t1,t2));

    taskService.bulkCompleteTasks(List.of(1L,2L));

    ArgumentCaptor<List<Task>> captor = ArgumentCaptor.forClass((Class)List.class);
    verify(taskRepository).saveAll(captor.capture());
    List<Task> saved = captor.getValue();
    assertThat(saved).hasSize(2);
    assertThat(saved).allMatch(Task::isCompleted);
  }

  @Test
  void bulkCompleteTasks_missingTask_throws() {
    when(taskRepository.findAllById(List.of(1L,2L))).thenReturn(List.of(new Task()));
    assertThrows(TaskNotFoundException.class, () -> taskService.bulkCompleteTasks(List.of(1L,2L)));
    verify(taskRepository, never()).saveAll(any());
  }
}
