package com.mipt.todo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * JPA-сущность задачи с метаданными приоритета, сроков и тегов.
 */
@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  private boolean completed;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  private LocalDate dueDate;

  @Enumerated(EnumType.STRING)
  private Priority priority;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "task_tags", joinColumns = @JoinColumn(name = "task_id"))
  @Column(name = "tag")
  private Set<String> tags = new HashSet<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<TaskAttachment> attachments = new HashSet<>();

  public Task() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public boolean isCompleted() { return completed; }
  public void setCompleted(boolean completed) { this.completed = completed; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

  public Priority getPriority() { return priority; }
  public void setPriority(Priority priority) { this.priority = priority; }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags != null ? tags : new HashSet<>(); }

  public Set<TaskAttachment> getAttachments() { return attachments; }
  public void setAttachments(Set<TaskAttachment> attachments) { this.attachments = attachments; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task)) return false;
    Task other = (Task) o;
    return id != null && id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return 31;
  }
}