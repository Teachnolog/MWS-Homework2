package com.mipt.todo.controller;

import com.mipt.todo.dto.AttachmentResponseDto;
import com.mipt.todo.model.TaskAttachment;
import com.mipt.todo.service.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Контроллер для управления вложениями к задачам.
 */
@RestController
@RequestMapping("/api")
public class AttachmentController {

  private final AttachmentService attachmentService;

  public AttachmentController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping("/tasks/{taskId}/attachments")
  public ResponseEntity<AttachmentResponseDto> uploadAttachment(
      @PathVariable Long taskId,
      @RequestParam("file") MultipartFile file) {
    try {
      TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
      if (attachment == null) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
      AttachmentResponseDto dto = new AttachmentResponseDto(
          attachment.getId(),
          attachment.getFileName(),
          attachment.getSize(),
          attachment.getUploadedAt()
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/attachments/{attachmentId}")
  public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
    try {
      java.util.Optional<TaskAttachment> maybe = attachmentService.getAttachment(attachmentId);
      if (maybe == null || maybe.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      TaskAttachment attachment = maybe.get();
      Resource resource = attachmentService.loadAsResource(attachmentId);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              ContentDisposition.attachment()
                  .filename(attachment.getFileName())
                  .build()
                  .toString())
          .header(HttpHeaders.CONTENT_TYPE, attachment.getContentType())
          .body(resource);
    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/attachments/{attachmentId}")
  public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
    try {
      attachmentService.deleteAttachment(attachmentId);
      return ResponseEntity.noContent().build();
    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/tasks/{taskId}/attachments")
  public ResponseEntity<List<AttachmentResponseDto>> getTaskAttachments(@PathVariable Long taskId) {
    List<TaskAttachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);
    List<AttachmentResponseDto> dtos = attachments.stream()
        .map(a -> new AttachmentResponseDto(a.getId(), a.getFileName(), a.getSize(), a.getUploadedAt()))
        .toList();
    return ResponseEntity.ok(dtos);
  }
}

