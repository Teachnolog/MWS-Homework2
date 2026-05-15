package com.mipt.todo.dto;

import com.mipt.todo.model.Priority;

/** Простая DTO для статистики задач по приоритетам */
public class TaskPriorityCountDto {
  private Priority priority;
  private long count;

  public TaskPriorityCountDto() {}

  public TaskPriorityCountDto(Priority priority, long count) {
    this.priority = priority;
    this.count = count;
  }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public long getCount() { return count; }
  public void setCount(long count) { this.count = count; }
}

