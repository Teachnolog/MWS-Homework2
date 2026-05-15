package com.mipt.todo.service;

import com.mipt.todo.model.TaskAttachment;
import com.mipt.todo.repository.TaskAttachmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Сервис для работы с вложениями к задачам.
 */
@Service
public class AttachmentService {

  private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);

  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  @Value("${app.upload.max-size:10485760}")
  private long maxFileSize;

  private final TaskAttachmentRepository attachmentRepository;

  public AttachmentService(TaskAttachmentRepository attachmentRepository) {
    this.attachmentRepository = attachmentRepository;
  }

  public TaskAttachment storeAttachment(Long taskId, MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File cannot be empty");
    }
    if (file.getSize() > maxFileSize) {
      throw new IllegalArgumentException("File size exceeds maximum allowed size");
    }

    Path uploadPath = Paths.get(uploadDir);
    Files.createDirectories(uploadPath);

    String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    Path filePath = uploadPath.resolve(storedFileName);

    Files.copy(file.getInputStream(), filePath);
    log.info("File stored: {}", filePath);

    TaskAttachment attachment = new TaskAttachment();
    attachment.setTaskId(taskId);
    attachment.setFileName(file.getOriginalFilename());
    attachment.setStoredFileName(storedFileName);
    attachment.setContentType(file.getContentType());
    attachment.setSize(file.getSize());
    attachment.setUploadedAt(LocalDateTime.now());

    return attachmentRepository.save(attachment);
  }

  public Resource loadAsResource(Long attachmentId) throws IOException {
    Optional<TaskAttachment> attachment = attachmentRepository.findById(attachmentId);
    if (attachment.isEmpty()) {
      throw new IOException("Attachment not found");
    }

    Path filePath = Paths.get(uploadDir).resolve(attachment.get().getStoredFileName());
    Resource resource = new FileSystemResource(filePath);

    if (!resource.exists()) {
      throw new IOException("File not found on disk");
    }

    return resource;
  }

  public Optional<TaskAttachment> getAttachment(Long attachmentId) {
    return attachmentRepository.findById(attachmentId);
  }

  public void deleteAttachment(Long attachmentId) throws IOException {
    Optional<TaskAttachment> attachment = attachmentRepository.findById(attachmentId);
    if (attachment.isPresent()) {
      Path filePath = Paths.get(uploadDir).resolve(attachment.get().getStoredFileName());
      Files.deleteIfExists(filePath);
      attachmentRepository.deleteById(attachmentId);
      log.info("Attachment deleted: {}", attachmentId);
    }
  }

  public List<TaskAttachment> getAttachmentsByTaskId(Long taskId) {
    return attachmentRepository.findByTaskId(taskId);
  }
}


