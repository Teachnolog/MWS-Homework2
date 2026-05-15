package com.mipt.todo.validation;

import com.mipt.todo.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Провайдер валидации для проверки что dueDate не ранее текущей даты.
 */
public class DueDateNotBeforeCreationValidator
    implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

  @Override
  public boolean isValid(TaskUpdateDto value, ConstraintValidatorContext context) {
    if (value == null || value.getDueDate() == null) {
      return true;
    }
    LocalDate today = LocalDate.now();
    return !value.getDueDate().isBefore(today);
  }
}

