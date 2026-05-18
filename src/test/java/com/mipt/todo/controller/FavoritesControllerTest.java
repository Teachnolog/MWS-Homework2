package com.mipt.todo.controller;

import com.mipt.todo.mapper.TaskMapper;
import com.mipt.todo.service.FavoritesService;
import com.mipt.todo.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для FavoritesController.
 */
@WebMvcTest(FavoritesController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoritesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @MockBean
  private FavoritesService favoritesService;

  @MockBean
  private TaskMapper taskMapper;

  @Test
  void addToFavorites_returns200() throws Exception {
    mockMvc.perform(post("/api/favorites/1"))
        .andExpect(status().isOk());
  }

  @Test
  void removeFromFavorites_returns200() throws Exception {
    mockMvc.perform(delete("/api/favorites/1"))
        .andExpect(status().isOk());
  }

  @Test
  void getFavorites_returns200() throws Exception {
    mockMvc.perform(get("/api/favorites"))
        .andExpect(status().isOk());
  }
}

