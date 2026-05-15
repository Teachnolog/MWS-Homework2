package com.mipt.todo.exception;

/**
 * Исключение, выбрасываемое когда задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {

  public TaskNotFoundException(String message) {
    super(message);
  }

  public TaskNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

