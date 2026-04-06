package com.mipt.todo.config;

import com.mipt.todo.repository.StubTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  @Bean(name = "stubTaskRepository")
  public StubTaskRepository stubTaskRepository() {
    return new StubTaskRepository();
  }
}