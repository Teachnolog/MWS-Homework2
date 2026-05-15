package com.mipt.todo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Валидация, проверяющая что dueDate не ранее даты создания.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateNotInPastValidator.class)
public @interface DueDateNotInPast {
  String message() default "Due date cannot be earlier than creation date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

