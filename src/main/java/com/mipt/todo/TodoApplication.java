package com.mipt.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Точка входа Spring Boot приложения для управления задачами.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class TodoApplication {
  public static void main(String[] args) {
    SpringApplication.run(TodoApplication.class, args);
  }
}