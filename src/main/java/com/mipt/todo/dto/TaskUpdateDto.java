package com.mipt.todo.dto;

import com.mipt.todo.validation.DueDateNotBeforeCreation;

import com.mipt.todo.model.Priority;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO для обновления существующей задачи.
 * Все поля опциональны для частичного обновления.
 */
@DueDateNotBeforeCreation
public class TaskUpdateDto {

  @Size(min = 3, max = 100, groups = OnUpdate.class, message = "Title length must be between 3 and 100")
  private String title;

  @Size(max = 500, groups = OnUpdate.class, message = "Description cannot exceed 500 characters")
  private String description;

  private Boolean completed;

  private LocalDate dueDate;

  private Priority priority;

  @Size(max = 5, groups = OnUpdate.class, message = "Cannot have more than 5 tags")
  private Set<String> tags;

  public TaskUpdateDto() {}

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Boolean getCompleted() { return completed; }
  public void setCompleted(Boolean completed) { this.completed = completed; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }
}

