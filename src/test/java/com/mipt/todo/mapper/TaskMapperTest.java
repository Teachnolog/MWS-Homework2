package com.mipt.todo.mapper;

import com.mipt.todo.dto.TaskCreateDto;
import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.model.Priority;
import com.mipt.todo.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для TaskMapper.
 */
@SpringBootTest
@ActiveProfiles("test")
class TaskMapperTest {

  @Autowired
  private TaskMapper taskMapper;

  @Test
  void toEntity_convertsCreateDtoToTask() {
    TaskCreateDto dto = new TaskCreateDto();
    dto.setTitle("Test Task");
    dto.setDescription("Test Description");
    dto.setDueDate(LocalDate.now().plusDays(1));
    dto.setPriority(Priority.HIGH);
    dto.setTags(new HashSet<>(Set.of("urgent", "work")));

    Task task = taskMapper.toEntity(dto);

    assertThat(task).isNotNull();
    assertThat(task.getTitle()).isEqualTo("Test Task");
    assertThat(task.getDescription()).isEqualTo("Test Description");
    assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
  }

  @Test
  void toResponseDto_convertTaskToResponseDto() {
    Task task = new Task();
    task.setId(1L);
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setCompleted(false);
    task.setPriority(Priority.LOW);

    TaskResponseDto dto = taskMapper.toResponseDto(task);

    assertThat(dto).isNotNull();
    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getTitle()).isEqualTo("Test Task");
    assertThat(dto.isCompleted()).isFalse();
  }
}

