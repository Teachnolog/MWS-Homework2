package com.mipt.todo.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Сервис для управления избранными задачами на основе сессии.
 */
@Service
public class FavoritesService {

  public void addToFavorites(Set<Long> favorites, Long taskId) {
    if (favorites == null) {
      favorites = new HashSet<>();
    }
    favorites.add(taskId);
  }

  public void removeFromFavorites(Set<Long> favorites, Long taskId) {
    if (favorites != null) {
      favorites.remove(taskId);
    }
  }

  public boolean isFavorite(Set<Long> favorites, Long taskId) {
    return favorites != null && favorites.contains(taskId);
  }
}

