package com.mipt.todo.validation;

import com.mipt.todo.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Провайдер валидации для проверки что dueDate не ранее текущей даты.
 */
public class DueDateNotInPastValidator
    implements ConstraintValidator<DueDateNotInPast, TaskUpdateDto> {

  @Override
  public boolean isValid(TaskUpdateDto value, ConstraintValidatorContext context) {
    if (value == null || value.getDueDate() == null) {
      return true;
    }
    return !value.getDueDate().isBefore(LocalDate.now());
  }
}

