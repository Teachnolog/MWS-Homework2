package com.mipt.todo.service;

import com.mipt.todo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TaskStatisticsService {

  private final TaskRepository primaryRepository;
  private final TaskRepository stubRepository;

  public TaskStatisticsService(TaskRepository primaryRepository,
      @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
    this.primaryRepository = primaryRepository;
    this.stubRepository = stubRepository;
  }

  public Map<String, Object> getStatistics() {
    Map<String, Object> stats = new LinkedHashMap<>();
    stats.put("primaryRepositoryTaskCount", primaryRepository.findAll().size());
    stats.put("stubRepositoryTaskCount", stubRepository.findAll().size());
    stats.put("stubTasks", stubRepository.findAll());
    return stats;
  }
}