package com.mipt.todo.dto;

import com.mipt.todo.model.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO для создания новой задачи.
 */
public class TaskCreateDto {

  @NotBlank(groups = OnCreate.class, message = "Title is required")
  @Size(min = 3, max = 100, groups = OnCreate.class, message = "Title length must be between 3 and 100")
  private String title;

  @Size(max = 500, groups = OnCreate.class, message = "Description cannot exceed 500 characters")
  private String description;

  @FutureOrPresent(groups = OnCreate.class, message = "Due date cannot be in the past")
  private LocalDate dueDate;

  @NotNull(groups = OnCreate.class, message = "Priority is required")
  private Priority priority;

  @Size(max = 5, groups = OnCreate.class, message = "Cannot have more than 5 tags")
  private Set<String> tags;

  public TaskCreateDto() {}

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }
}

