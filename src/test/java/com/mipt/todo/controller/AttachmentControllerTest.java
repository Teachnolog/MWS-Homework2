package com.mipt.todo.controller;

import com.mipt.todo.model.TaskAttachment;
import com.mipt.todo.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttachmentService attachmentService;

  @Test
  void uploadAttachment_withValidFile_returns201() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes()
    );

    TaskAttachment attachment = new TaskAttachment();
    attachment.setId(1L);
    attachment.setFileName("test.txt");
    attachment.setSize((long) file.getSize());
    attachment.setUploadedAt(LocalDateTime.now());

    when(attachmentService.storeAttachment(eq(1L), any())).thenReturn(attachment);

    mockMvc.perform(multipart("/api/tasks/1/attachments").file(file))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.fileName").value("test.txt"));
  }

  @Test
  void downloadAttachment_withInvalidId_returns404() throws Exception {
    when(attachmentService.getAttachment(999999L)).thenReturn(java.util.Optional.empty());

    mockMvc.perform(get("/api/attachments/999999"))
            .andExpect(status().isNotFound());
  }

  @Test
  void deleteAttachment_withValidId_returns204() throws Exception {
    doNothing().when(attachmentService).deleteAttachment(1L);

    mockMvc.perform(delete("/api/attachments/1"))
            .andExpect(status().isNoContent());
  }

  @Test
  void getTaskAttachments_returns200() throws Exception {
    when(attachmentService.getAttachmentsByTaskId(1L)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/tasks/1/attachments"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }
}