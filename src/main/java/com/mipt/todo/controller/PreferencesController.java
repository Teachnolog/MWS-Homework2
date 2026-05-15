package com.mipt.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Контроллер для управления настройками пользователя и куками.
 */
@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

  private static final String VIEW_PREF_COOKIE = "viewPreference";

  @GetMapping("/view")
  public ResponseEntity<java.util.Map<String, String>> getViewPreference(
      @CookieValue(value = VIEW_PREF_COOKIE, defaultValue = "detailed") String viewPref) {
    return ResponseEntity.ok(java.util.Collections.singletonMap("view", viewPref));
  }

  @PostMapping("/view")
  public ResponseEntity<java.util.Map<String, String>> setViewPreference(
      @RequestParam String mode,
      HttpServletResponse response) {
    if (!mode.equals("compact") && !mode.equals("detailed")) {
      return ResponseEntity.badRequest().build();
    }
    response.addCookie(createCookie(VIEW_PREF_COOKIE, mode));
    return ResponseEntity.ok(java.util.Collections.singletonMap("view", mode));
  }

  private jakarta.servlet.http.Cookie createCookie(String name, String value) {
    jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(name, value);
    cookie.setPath("/");
    cookie.setMaxAge(30 * 24 * 60 * 60); // 30 дней
    cookie.setHttpOnly(true);
    return cookie;
  }
}

