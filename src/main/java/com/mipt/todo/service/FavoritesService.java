package com.mipt.todo.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Сервис для управления избранными задачами на основе сессии.
 */
@Service
public class FavoritesService {

  public Set<Long> addToFavorites(Set<Long> favorites, Long taskId) {
    if (favorites == null) {
      favorites = new HashSet<>();
    }
    favorites.add(taskId);
    return favorites;
  }

  public Set<Long> removeFromFavorites(Set<Long> favorites, Long taskId) {
    if (favorites != null) {
      favorites.remove(taskId);
    }
    return favorites;
  }

  public boolean isFavorite(Set<Long> favorites, Long taskId) {
    return favorites != null && favorites.contains(taskId);
  }
}

