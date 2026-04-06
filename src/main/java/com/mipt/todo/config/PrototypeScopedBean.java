package com.mipt.todo.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeScopedBean {

  public String generateId() {
    return UUID.randomUUID().toString();
  }
}