package com.mipt.todo.config;

import com.mipt.todo.repository.StubTaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Основная конфигурация приложения, где объявляется stub-репозиторий как bean.
 */
@Configuration
public class AppConfig {


  @Bean(name = "stubTaskRepository")
  public StubTaskRepository stubTaskRepository() {
    return new StubTaskRepository();
  }

  @Bean
  @Scope("prototype")
  public PrototypeScopedBean prototypeScopedBean() {
    return new PrototypeScopedBean();
  }
}