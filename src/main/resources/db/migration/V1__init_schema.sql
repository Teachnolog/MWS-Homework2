-- Flyway migration: create tasks, task_attachments and task_tags
CREATE TABLE tasks (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(1024) NOT NULL,
  description TEXT,
  completed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  due_date DATE,
  priority VARCHAR(50)
);

CREATE TABLE task_attachments (
  id BIGSERIAL PRIMARY KEY,
  task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  file_name VARCHAR(1024),
  stored_file_name VARCHAR(1024),
  content_type VARCHAR(255),
  size BIGINT,
  uploaded_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE task_tags (
  task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
  tag VARCHAR(255) NOT NULL
);

CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_task_tags_tag ON task_tags(tag);

