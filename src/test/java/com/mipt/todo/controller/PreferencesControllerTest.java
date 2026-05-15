package com.mipt.todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для PreferencesController.
 */
@WebMvcTest(PreferencesController.class)
class PreferencesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getViewPreference_returns200() throws Exception {
    mockMvc.perform(get("/api/preferences/view"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void setViewPreference_withCompactMode_returns200() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
        .param("mode", "compact"))
        .andExpect(status().isOk());
  }

  @Test
  void setViewPreference_withDetailedMode_returns200() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
        .param("mode", "detailed"))
        .andExpect(status().isOk());
  }

  @Test
  void setViewPreference_withInvalidMode_returns400() throws Exception {
    mockMvc.perform(post("/api/preferences/view")
        .param("mode", "invalid"))
        .andExpect(status().isBadRequest());
  }
}

