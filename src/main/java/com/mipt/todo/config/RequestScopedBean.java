package com.mipt.todo.config;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {

  private final String requestId;
  private final LocalDateTime startTime;

  public RequestScopedBean() {
    this.requestId = UUID.randomUUID().toString();
    this.startTime = LocalDateTime.now();
  }

  public String getRequestId() { return requestId; }

  public LocalDateTime getStartTime() { return startTime; }
}