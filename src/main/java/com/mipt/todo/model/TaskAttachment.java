package com.mipt.todo.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * JPA-сущность вложения к задаче.
 */
@Entity
@Table(name = "task_attachments")
public class TaskAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  private String fileName;
  private String storedFileName;
  private String contentType;
  private long size;
  private LocalDateTime uploadedAt;

  public TaskAttachment() {}

  public TaskAttachment(Long id, Task task, String fileName, String storedFileName,
      String contentType, long size, LocalDateTime uploadedAt) {
    this.id = id;
    this.task = task;
    this.fileName = fileName;
    this.storedFileName = storedFileName;
    this.contentType = contentType;
    this.size = size;
    this.uploadedAt = uploadedAt;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Task getTask() { return task; }
  public void setTask(Task task) { this.task = task; }

  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }

  public String getStoredFileName() { return storedFileName; }
  public void setStoredFileName(String storedFileName) { this.storedFileName = storedFileName; }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }

  public long getSize() { return size; }
  public void setSize(long size) { this.size = size; }

  public LocalDateTime getUploadedAt() { return uploadedAt; }
  public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
