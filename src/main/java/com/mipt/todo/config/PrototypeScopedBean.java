package com.mipt.todo.config;

import java.util.UUID;

/**
 * Bean со scope prototype, генерирующий новый идентификатор для каждой инстанции.
 */
public class PrototypeScopedBean {

  public Long generateTaskId() {
    UUID uuid = UUID.randomUUID();
    long candidate = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
    return candidate == Long.MIN_VALUE ? 0L : Math.abs(candidate);
  }
}