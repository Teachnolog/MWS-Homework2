package com.mipt.todo.exception;

import com.mipt.todo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    Map<String, Object> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String message = error.getDefaultMessage();
      if (error instanceof FieldError fieldError) {
        errors.put(fieldError.getField(), message);
      } else {
        errors.put(error.getObjectName(), message);
      }
    });

    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        "Validation failed",
        request.getRequestURI()
    );
    errorResponse.setDetails(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFound(
      TaskNotFoundException ex,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFound(
      NoHandlerFoundException ex,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        "Endpoint not found: " + request.getRequestURI(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        ex.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex,
      HttpServletRequest request) {
    log.error("Unexpected error: ", ex);
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "An unexpected error occurred",
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}

