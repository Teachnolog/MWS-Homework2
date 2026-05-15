package com.mipt.todo.dto;

import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о вложении.
 */
public class AttachmentResponseDto {

  private Long id;
  private String fileName;
  private long size;
  private LocalDateTime uploadedAt;

  public AttachmentResponseDto() {}

  public AttachmentResponseDto(Long id, String fileName, long size, LocalDateTime uploadedAt) {
    this.id = id;
    this.fileName = fileName;
    this.size = size;
    this.uploadedAt = uploadedAt;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }

  public long getSize() { return size; }
  public void setSize(long size) { this.size = size; }

  public LocalDateTime getUploadedAt() { return uploadedAt; }
  public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}

