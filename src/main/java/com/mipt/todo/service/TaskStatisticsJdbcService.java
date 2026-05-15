package com.mipt.todo.service;

import com.mipt.todo.dto.TaskPriorityCountDto;
import com.mipt.todo.model.Priority;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/** Простая служба, демонстрирующая использование JdbcTemplate */
@Service
public class TaskStatisticsJdbcService {

  private final JdbcTemplate jdbcTemplate;

  public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<TaskPriorityCountDto> getTasksCountByPriority() {
    String sql = "SELECT priority, count(*) as cnt FROM tasks GROUP BY priority";
    return jdbcTemplate.query(sql, new RowMapper<TaskPriorityCountDto>() {
      @Override
      public TaskPriorityCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        String p = rs.getString("priority");
        Priority priority = p != null ? Priority.valueOf(p) : null;
        long cnt = rs.getLong("cnt");
        return new TaskPriorityCountDto(priority, cnt);
      }
    });
  }
}

