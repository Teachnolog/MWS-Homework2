package com.mipt.todo.controller;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

  @GetMapping("/profile")
  public Map<String, Object> profile(Authentication authentication) {
    return Map.of(
        "username", authentication.getName(),
        "authorities", authentication.getAuthorities().stream().map(Object::toString).toList());
  }

  @GetMapping("/docs")
  public Map<String, String> docs() {
    return Map.of("message", "Protected docs endpoint is available");
  }
}

